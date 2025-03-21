package dmsrosa.dns_server;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import dmsrosa.dns_server.messages.DnsPacket;
import dmsrosa.dns_server.messages.DnsRecord;
import dmsrosa.dns_server.messages.NSRecord;
import dmsrosa.dns_server.messages.QueryType;
import dmsrosa.dns_server.messages.ResultCode;

public class Cache {
    public static class RecordEntry {
        private final DnsRecord record;
        private final LocalDateTime timestamp;

        public RecordEntry(DnsRecord record) {
            this.record = record;
            this.timestamp = LocalDateTime.now();
        }

        public DnsRecord getRecord() {
            return record;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (!(o instanceof RecordEntry that))
                return false;
            return Objects.equals(record, that.record);
        }

        @Override
        public int hashCode() {
            return Objects.hash(record);
        }
    }

    enum CacheState {
        PositiveCache,
        NegativeCache,
        NotCached
    }

    abstract static class RecordSet {
        protected final QueryType qtype;

        public RecordSet(QueryType qtype) {
            this.qtype = qtype;
        }

        public QueryType getQueryType() {
            return qtype;
        }
    }

    class PositiveRecordSet extends RecordSet {
        private final Set<RecordEntry> records;

        public PositiveRecordSet(QueryType qtype) {
            super(qtype);
            this.records = new HashSet<>();
        }

        public void addRecord(DnsRecord rec) {
            RecordEntry entry = new RecordEntry(rec);
            records.remove(entry);
            records.add(entry);
        }

        public Set<RecordEntry> getRecords() {
            return records;
        }
    }

    class NegativeRecordSet extends RecordSet {
        private final int ttl;
        private final LocalDateTime timestamp;

        public NegativeRecordSet(QueryType qtype, int ttl) {
            super(qtype);
            this.ttl = ttl;
            this.timestamp = LocalDateTime.now();
        }

        public int getTtl() {
            return ttl;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }
    }

    public class DomainEntry {
        private final String domain;
        private final Map<Integer, RecordSet> recordTypes;
        private int hits;
        private int updates;

        public DomainEntry(String domain) {
            this.domain = domain;
            this.recordTypes = new HashMap<>();
            this.hits = 0;
            this.updates = 0;
        }

        public void storeRecord(DnsRecord rec) {
            updates++;
            QueryType qt = rec.getQueryType();
            RecordSet set = recordTypes.get(qt.getValue());
            if (!(set instanceof PositiveRecordSet)) {
                set = new PositiveRecordSet(qt);
                recordTypes.put(qt.getValue(), set);
            }
            ((PositiveRecordSet) set).addRecord(rec);
        }

        public void storeNXDomain(QueryType qtype, int ttl) {
            updates++;
            recordTypes.put(qtype.getValue(), new NegativeRecordSet(qtype, ttl));
        }

        private CacheState getCacheState(QueryType qtype) {
            RecordSet set = recordTypes.get(qtype.getValue());
            if (set == null) {
                return CacheState.NotCached;
            }
            if (set instanceof PositiveRecordSet) {
                LocalDateTime now = LocalDateTime.now();
                int validCount = 0;
                for (RecordEntry entry : ((PositiveRecordSet) set).getRecords()) {
                    Duration ttlDuration = Duration.ofSeconds(entry.getRecord().getTtl());
                    LocalDateTime expires = entry.getTimestamp().plus(ttlDuration);
                    if (expires.isAfter(now)) {
                        validCount++;
                    }
                }
                return validCount > 0 ? CacheState.PositiveCache : CacheState.NotCached;
            } else if (set instanceof NegativeRecordSet nset) {
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime expires = nset.getTimestamp().plus(Duration.ofSeconds(nset.getTtl()));
                return expires.isAfter(now) ? CacheState.NegativeCache : CacheState.NotCached;
            }
            return CacheState.NotCached;
        }

        public <T extends DnsRecord> void fillQueryResult(QueryType qtype, List<T> resultList, Class<T> cls) {
            RecordSet set = recordTypes.get(qtype.getValue());
            if (!(set instanceof PositiveRecordSet)) {
                return;
            }
            LocalDateTime now = LocalDateTime.now();
            for (RecordEntry entry : ((PositiveRecordSet) set).getRecords()) {
                Duration ttlDuration = Duration.ofSeconds(entry.getRecord().getTtl());
                LocalDateTime expires = entry.getTimestamp().plus(ttlDuration);
                if (expires.isAfter(now)) {
                    DnsRecord record = entry.getRecord();
                    if (cls.isInstance(record)) {
                        resultList.add(cls.cast(record));
                    }
                }
            }
        }

        public void incrementHits() {
            hits++;
        }
    }

    private final SortedMap<String, DomainEntry> domainEntries;

    public Cache() {
        this.domainEntries = new TreeMap<>();
    }

    public synchronized DnsPacket lookup(String qname, QueryType qtype) {
        DomainEntry entry = domainEntries.get(qname);
        if (entry == null) {
            return null;
        }
        CacheState state = entry.getCacheState(qtype);
        if (state == CacheState.PositiveCache) {
            DnsPacket packet = new DnsPacket();
            entry.incrementHits();
            entry.fillQueryResult(qtype, packet.getAnswers(), DnsRecord.class);
            entry.fillQueryResult(new QueryType.NSQueryType(), packet.getAuthorities(), NSRecord.class);
            return packet;
        } else if (state == CacheState.NegativeCache) {
            DnsPacket packet = new DnsPacket();
            packet.getHeader().setRCODE(ResultCode.NX_DOMAIN);
            return packet;
        }
        return null;
    }

    public synchronized void store(DnsRecord[] records) {
        for (DnsRecord rec : records) {
            String domain = rec.getDomain();
            if (domain == null) {
                continue;
            }
            DomainEntry entry = domainEntries.computeIfAbsent(domain, DomainEntry::new);
            entry.storeRecord(rec);
        }
    }

    public synchronized void store(List<DnsRecord> records) {
        for (DnsRecord rec : records) {
            String domain = rec.getDomain();
            if (domain == null) {
                continue;
            }
            DomainEntry entry = domainEntries.computeIfAbsent(domain, DomainEntry::new);
            entry.storeRecord(rec);
        }
    }

    public synchronized void storeNXDomain(String qname, QueryType qtype, int ttl) {
        DomainEntry entry = domainEntries.computeIfAbsent(qname, DomainEntry::new);
        entry.storeNXDomain(qtype, ttl);
    }
}
