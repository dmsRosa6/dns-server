package dmsrosa.dns_server;

import java.util.List;
import java.util.Map;

import dmsrosa.dns_server.messages.DnsRecord;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

public class Controller {

    private final ZoneStorage zoneStorage;
    private final Javalin app;

    public Controller(ZoneStorage zoneStorage) {
        this.zoneStorage = zoneStorage;
        this.app = Javalin.create().start(7070);

        app.post("/records", this::createRecord);

        app.get("/records/{domain}", this::getRecordsByDomain);

        app.get("/records", this::getAllRecords);
    }

    public static class DnsRecordDTO {
        public String domain;
        public int type;
        public String value;
        public int ttl;
    }

    private void createRecord(Context ctx) {
        DnsRecordDTO recordDTO = ctx.bodyAsClass(DnsRecordDTO.class);

        if (recordDTO.domain == null || recordDTO.value == null) {
            ctx.status(HttpStatus.BAD_REQUEST).result("Domain and value are required.");
            return;
        }

        boolean success = zoneStorage.addRecord(recordDTO.domain, recordDTO.type, recordDTO.value, recordDTO.ttl);
        if (success) {
            ctx.status(HttpStatus.CREATED).result("Record created successfully.");
        } else {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).result("Failed to create record.");
        }
    }

    private void getAllRecords(Context ctx) {
        Map<String, Map<Integer, List<DnsRecord>>> records = zoneStorage.getAllRecords();

        if (records == null || records.isEmpty()) {
            ctx.status(HttpStatus.NOT_FOUND).result("No records found.");
        } else {
            ctx.json(records);
        }

    }

    private void getRecordsByDomain(Context ctx) {
        String domain = ctx.pathParam("domain");

        Map<Integer, List<DnsRecord>> records = zoneStorage.getRecordsByDomain(domain);

        if (records == null || records.isEmpty()) {
            ctx.status(HttpStatus.NOT_FOUND).result("No records found for the specified domain.");
        } else {
            ctx.json(records);
        }
    }
}
