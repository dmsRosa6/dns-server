# DNS Server Project

This project is a basic DNS server implementation inspired by the [DNS Guide by Emil Hernvall](https://github.com/EmilHernvall/dnsguide/tree/master) and built according to the specifications defined in [RFC 1035](https://tools.ietf.org/html/rfc1035). The server is designed to handle a limited set of DNS query types and provide simple DNS resolution functionality.

## Features

- **Basic DNS Query Handling:**  
  The server can process DNS queries by reading incoming UDP packets and sending appropriate responses based on the request.  
- **Supported Query Types:**  
  - **A Record:** Maps a domain name to an IPv4 address.
  - **NS Record:** Provides the authoritative name server for the domain.
  - **CNAME Record:** Specifies the canonical name for an alias.
  - **MX Record:** Indicates the mail exchange server responsible for the domain.
  - **AAAA Record:** Maps a domain name to an IPv6 address.
- **Packet Parsing and Serialization:**  
  The server implements methods to read and write DNS packets, conforming to the format described in RFC 1035.

## How It Works

1. **Receiving Queries:**  
   The server listens for UDP packets on a specified port (default 2053) and parses the DNS query.
2. **Processing Requests:**  
   The DNS query is parsed into its components (header, questions, etc.) and handled appropriately by the server.
3. **Generating Responses:**  
   The server builds a DNS response packet by setting the proper header flags, including the response code, and adds answers, authorities, and additional resource records as required.
4. **Forwarding Queries:**  
   For queries that require external resolution (e.g., looking up records from public DNS servers like 8.8.8.8), the server forwards the request and relays the response.

## References

- **DNS Guide by Emil Hernvall:**  
  [dnsguide on GitHub](https://github.com/EmilHernvall/dnsguide/tree/master)
- **RFC 1035:**  
  [Domain Names - Implementation and Specification](https://tools.ietf.org/html/rfc1035)

## Future Work

To further improve the project, the following enhancements could be cool:

- **More query types:**  
  A lot of query types are missing on the implementation.

- **Concurrency:**  
  The server currently lacks true concurrency. It processes one query at a time.
  
- **TCP Support:**  
  The server does not support DNS queries over TCP. This is a limitation since TCP is required for handling larger DNS responses and for certain types of queries.
  
- **Authoritative Zones:**  
  There is no capability to host your own DNS zones or act as an authoritative DNS server.
  
- **DNSSEC Support:**  
  The absence of DNSSEC means the server is vulnerable to DNS poisoning attacks, where a malicious server could return falsified records for a domain.

---

This project is a work in progress and aims to serve as a learning tool.
