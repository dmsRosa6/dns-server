package dmsrosa.dns_server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dmsrosa.dns_server.messages.ARecord;
import dmsrosa.dns_server.messages.DnsPacket;
import dmsrosa.dns_server.messages.DnsRecord;
import dmsrosa.dns_server.messages.QueryType;

public class ZoneStorage {

    private Map<String, Map<Integer, List<DnsRecord>>> storage;
    private static final String STORAGE_PATH = "storage.txt";
    private static final String DELIMITER = ":";

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
                                    record = new ARecord(domain, addr);
                                } else {
                                    System.err.println("Invalid A record format: " + line);
                                }
                                break;
                            // Handle other record types (e.g., AAAA, MX) here
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
                        System.err.println("Invalid IP address: " + parts[2].trim());
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
}
