package dmsrosa.dns_server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dmsrosa.dns_server.messages.AAAARecord;
import dmsrosa.dns_server.messages.ARecord;
import dmsrosa.dns_server.messages.CNameRecord;
import dmsrosa.dns_server.messages.DnsPacket;
import dmsrosa.dns_server.messages.DnsRecord;
import dmsrosa.dns_server.messages.MXRecord;
import dmsrosa.dns_server.messages.NSRecord;
import dmsrosa.dns_server.messages.OPTRecord;
import dmsrosa.dns_server.messages.QueryType;
import dmsrosa.dns_server.messages.SOARecord;
import dmsrosa.dns_server.messages.SRVRecord;
import dmsrosa.dns_server.messages.TXTRecord;

public class ZoneStorage {

    private Map<String, Map<Integer, List<DnsRecord>>> storage;
    private static final String STORAGE_PATH = "storage.txt";
    private static final String DELIMITER = ":";
    private static final int DEFAULT_TTL = 3600;
    private static final int DEFAULT_OPT_PACKET_LEN = 4096;

    public ZoneStorage() {
        loadStorage();
    }

    private void loadStorage() {
        storage = new HashMap<>();
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(STORAGE_PATH)) {
            if (is == null) {
                System.out.println("Storage file not found in resources: " + STORAGE_PATH);
                return;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                String[] parts = line.split(DELIMITER);
                if (parts.length >= 2) {
                    try {
                        int qType = Integer.parseInt(parts[0].trim());
                        String domain = parts[1].trim();

                        DnsRecord record = null;
                        switch (qType) {
                            case 1: // A record
                                if (parts.length == 3) {
                                    String ipString = parts[2].trim();
                                    Inet4Address addr = (Inet4Address) InetAddress.getByName(ipString);
                                    record = new ARecord(domain, addr, DEFAULT_TTL);
                                } else {
                                    System.err.println("Invalid A record format: " + line);
                                }
                                break;
                            case 28: // AAAA record
                                if (parts.length == 3) {
                                    String ipString = parts[2].trim();
                                    InetAddress ipv6 = InetAddress.getByName(ipString);
                                    record = new AAAARecord(domain, (Inet6Address) ipv6, DEFAULT_TTL);
                                } else {
                                    System.err.println("Invalid AAAA record format: " + line);
                                }
                                break;
                            case 5: // CNAME record
                                if (parts.length == 3) {
                                    String cname = parts[2].trim();
                                    record = new CNameRecord(domain, cname, DEFAULT_TTL);
                                } else {
                                    System.err.println("Invalid CNAME record format: " + line);
                                }
                                break;
                            case 15: // MX record
                                if (parts.length == 3) {
                                    // Expected format: priority,mailServer (comma separated)
                                    String[] mxParts = parts[2].trim().split(",");
                                    if (mxParts.length == 2) {
                                        int priority = Integer.parseInt(mxParts[0].trim());
                                        String mailServer = mxParts[1].trim();
                                        record = new MXRecord(domain, mailServer, (short) priority, DEFAULT_TTL);
                                    } else {
                                        System.err.println("Invalid MX record format: " + line);
                                    }
                                } else {
                                    System.err.println("Invalid MX record format: " + line);
                                }
                                break;
                            case 2: // NS record
                                if (parts.length == 3) {
                                    String ns = parts[2].trim();
                                    record = new NSRecord(domain, ns, DEFAULT_TTL);
                                } else {
                                    System.err.println("Invalid NS record format: " + line);
                                }
                                break;
                            case 41: // OPT record
                                if (parts.length == 3) {
                                    String optData = parts[2].trim();
                                    record = new OPTRecord((short) DEFAULT_OPT_PACKET_LEN, 0, optData);
                                } else {
                                    System.err.println("Invalid OPT record format: " + line);
                                }
                                break;
                            case 33: // SRV record
                                if (parts.length == 3) {
                                    String[] srvParts = parts[2].trim().split("\\s+");
                                    if (srvParts.length == 4) {
                                        int priority = Integer.parseInt(srvParts[0].trim());
                                        int weight = Integer.parseInt(srvParts[1].trim());
                                        int port = Integer.parseInt(srvParts[2].trim());
                                        String target = srvParts[3].trim();
                                        record = new SRVRecord((short) priority, (short) weight, (short) port, target,
                                                domain, DEFAULT_TTL);
                                    } else {
                                        System.err.println("Invalid SRV record format: " + line);
                                    }
                                } else {
                                    System.err.println("Invalid SRV record format: " + line);
                                }
                                break;
                            case 6: // SOA record
                                if (parts.length == 3) {
                                    String[] soaParts = parts[2].trim().split("\\s+");
                                    if (soaParts.length == 7) {
                                        String mname = soaParts[0].trim();
                                        String rname = soaParts[1].trim();
                                        int serial = Integer.parseInt(soaParts[2].trim());
                                        int refresh = Integer.parseInt(soaParts[3].trim());
                                        int retry = Integer.parseInt(soaParts[4].trim());
                                        int expire = Integer.parseInt(soaParts[5].trim());
                                        int minimum = Integer.parseInt(soaParts[6].trim());
                                        record = new SOARecord(mname, rname, serial, refresh,
                                                retry, expire, minimum, domain, DEFAULT_TTL);
                                    } else {
                                        System.err.println("Invalid SOA record format: " + line);
                                    }
                                } else {
                                    System.err.println("Invalid SOA record format: " + line);
                                }
                                break;
                            case 16: // TXT record
                                if (parts.length == 3) {
                                    String txtData = parts[2].trim();
                                    record = new TXTRecord(domain, txtData, DEFAULT_TTL);
                                } else {
                                    System.err.println("Invalid TXT record format: " + line);
                                }
                                break;
                            default:
                                System.err.println("Unsupported record type: " + qType);
                        }

                        if (record != null) {
                            storage.computeIfAbsent(domain, k -> new HashMap<>())
                                    .computeIfAbsent(qType, k -> new ArrayList<>())
                                    .add(record);
                        }
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid record type: " + parts[0].trim());
                    } catch (UnknownHostException e) {
                        System.err.println("Invalid IP address in line: " + line);
                    }
                } else {
                    System.err.println("Malformed line in storage file: " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading storage file: " + STORAGE_PATH);
            e.printStackTrace();
        }
    }

    public DnsPacket lookup(String qname, QueryType qt) {
        List<DnsRecord> l = null;
        try {
            l = storage.get(qname).get(qt.getValue());

            if (l == null) {
                return null;
            }
        } catch (NullPointerException e) {
            return null;
        }

        DnsPacket packet = new DnsPacket();

        packet.getHeader().setAA(true);
        packet.getHeader().setResponse(true);
        packet.setAnswers(l);

        return packet;
    }

    public boolean addRecord(String domain, int type, String data, int ttl) {
        DnsRecord record = createRecord(domain, type, data, ttl);
        if (record == null) {
            return false;
        }

        storage.computeIfAbsent(domain, k -> new HashMap<>())
                .computeIfAbsent(type, k -> new ArrayList<>())
                .add(record);
        return true;
    }

    public Map<Integer, List<DnsRecord>> getRecordsByDomain(String domain) {

        return storage.get(domain);
    }

    public Map<String, Map<Integer, List<DnsRecord>>> getAllRecords() {
        return storage;
    }

    private DnsRecord createRecord(String domain, int type, String data, int ttl) {
        try {
            switch (type) {
                case 1:
                    Inet4Address ipv4Address = (Inet4Address) InetAddress.getByName(data);
                    return new ARecord(domain, ipv4Address, ttl);
                case 28:
                    Inet6Address ipv6Address = (Inet6Address) InetAddress.getByName(data);
                    return new AAAARecord(domain, ipv6Address, ttl);
                case 5:
                    return new CNameRecord(domain, data, ttl);
                case 15:
                    String[] mxData = data.split("\\s+");
                    if (mxData.length != 2)
                        return null;
                    int priority = Integer.parseInt(mxData[0]);
                    String mailServer = mxData[1];
                    return new MXRecord(domain, mailServer, (short) priority, ttl);
                case 2:
                    return new NSRecord(domain, data, ttl);
                case 33:
                    String[] srvData = data.split("\\s+");
                    if (srvData.length != 4)
                        return null;
                    int srvPriority = Integer.parseInt(srvData[0]);
                    int weight = Integer.parseInt(srvData[1]);
                    int port = Integer.parseInt(srvData[2]);
                    String target = srvData[3];
                    return new SRVRecord((short) srvPriority, (short) weight, (short) port, target, domain, ttl);
                case 6:
                    String[] soaData = data.split("\\s+");
                    if (soaData.length != 7)
                        return null;
                    String mname = soaData[0];
                    String rname = soaData[1];
                    int serial = Integer.parseInt(soaData[2]);
                    int refresh = Integer.parseInt(soaData[3]);
                    int retry = Integer.parseInt(soaData[4]);
                    int expire = Integer.parseInt(soaData[5]);
                    int minimum = Integer.parseInt(soaData[6]);
                    return new SOARecord(mname, rname, serial, refresh, retry, expire, minimum, domain, ttl);
                case 16:
                    return new TXTRecord(data, domain, ttl);
                default:
                    System.err.println("Unsupported record type: " + type);
                    return null;
            }
        } catch (UnknownHostException | NumberFormatException e) {
            System.err.println("Invalid data for record type " + type + ": " + data);
            return null;
        }
    }

}
