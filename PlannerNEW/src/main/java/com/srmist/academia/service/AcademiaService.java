package com.srmist.academia.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.srmist.academia.controller.AcademiaDataController;
import com.srmist.academia.dto.*;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.time.Year;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AcademiaService {

    private static final String BASE_URL = "https://academia.srmist.edu.in";
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String loginToAcademia(String email, String password) throws Exception {
        try (CloseableHttpClient client = HttpClients.custom().build()) {

            // -------- Step 1: Lookup --------
            String lookupUrl = String.format("%s/accounts/p/40-10002227248/signin/v2/lookup/%s", BASE_URL, email);
            List<BasicNameValuePair> lookupParams = Arrays.asList(new BasicNameValuePair("mode", "primary"), new BasicNameValuePair("cli_time", String.valueOf(System.currentTimeMillis() / 1000)), new BasicNameValuePair("servicename", "ZohoCreator"), new BasicNameValuePair("service_language", "en"), new BasicNameValuePair("serviceurl", BASE_URL + "/portal/academia-academic-services/redirectFromLogin"));

            HttpPost lookupReq = new HttpPost(lookupUrl);
            lookupReq.setEntity(new UrlEncodedFormEntity(lookupParams));
            lookupReq.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
            lookupReq.setHeader("Accept", "application/json");
            lookupReq.setHeader("Accept-Language", "en-US,en;q=0.9");
            lookupReq.setHeader("sec-sh-ua", "\"Not(A:Brand\";v=\"99\", \"Google Chrome\";v=\"133\", \"Chromium\";v=\"133\"");
            lookupReq.setHeader("sec-sh-ua-mobile", "?0");
            lookupReq.setHeader("sec-sh-ua-platform", "'macOS'");
            lookupReq.setHeader("sec-fetch-dest", "empty");
            lookupReq.setHeader("sec-fetch-mode", "cors");
            lookupReq.setHeader("sec-fetch-site", "same-origin");
            lookupReq.setHeader("x-zcsrf-token", "iamcsrcoo=3c59613cb190a67effa5b17eaba832ef1eddaabeb7610c8c6a518b753bc73848b483b007a63f24d94d67d14dda0eca9f0c69e027c0ebd1bb395e51b2c6291d63");
            lookupReq.setHeader("cookie", "npfwg=1; npf_r=; npf_l=www.srmist.edu.in; npf_u=https://www.srmist.edu.in/faculty/dr-g-y-rajaa-vikhram/; zalb_74c3a1eecc=44130d4069ebce16724b1740d9128cae; ZCNEWUIPUBLICPORTAL=true; zalb_f0e8db9d3d=93b1234ae1d3e88e54aa74d5fbaba677; stk=efbb3889860a8a5d4a9ad34903359b4e; zccpn=3c59613cb190a67effa5b17eaba832ef1eddaabeb7610c8c6a518b753bc73848b483b007a63f24d94d67d14dda0eca9f0c69e027c0ebd1bb395e51b2c6291d63; zalb_3309580ed5=2f3ce51134775cd955d0a3f00a177578; CT_CSRF_TOKEN=9d0ab1e6-9f71-40fd-826e-7229d199b64d; iamcsr=3c59613cb190a67effa5b17eaba832ef1eddaabeb7610c8c6a518b753bc73848b483b007a63f24d94d67d14dda0eca9f0c69e027c0ebd1bb395e51b2c6291d63; _zcsr_tmp=3c59613cb190a67effa5b17eaba832ef1eddaabeb7610c8c6a518b753bc73848b483b007a63f24d94d67d14dda0eca9f0c69e027c0ebd1bb395e51b2c6291d63; npf_fx=1; _ga_QNCRQG0GFE=GS1.1.1737645192.5.0.1737645194.58.0.0; TS014f04d9=0190f757c98d895868ec35d391f7090a39080dd8e7be840ed996d7e2827e600c5b646207bb76666e56e22bfaf8d2c06ec3c913fe80; cli_rgn=IN; JSESSIONID=E78E4C7013F0D931BD251EBA136D57AE; _ga=GA1.3.1900970259.1737341486; _gid=GA1.3.1348593805.1737687406; _gat=1; _ga_HQWPLLNMKY=GS1.3.1737687405.1.0.1737687405.0.0.0");
            lookupReq.setHeader("Referer", (BASE_URL + "/accounts/p/10002227248/signin?hide_fp=true&servicename=ZohoCreator&service_language=en&css_url=/49910842/academia-academic-services/downloadPortalCustomCss/login&dcc=true&serviceurl=" + BASE_URL + "%2Fportal%2Facademia-academic-services%2FredirectFromLogin"));
            lookupReq.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");
            JsonNode lookupResp;

            try (CloseableHttpResponse resp = client.execute(lookupReq)) {
                int statusCode = resp.getCode();
                if (statusCode >= 400) {
                    Logger.getLogger("AcademiaService").warning("Lookup response: " + new String(resp.getEntity().getContent().readAllBytes()));
                    throw new RuntimeException("Lookup failed with status: " + statusCode);
                }
                lookupResp = objectMapper.readTree(resp.getEntity().getContent());
            }

            if (lookupResp.has("errors") && !lookupResp.get("errors").isEmpty()) {
                throw new RuntimeException("Lookup failed: " + lookupResp.get("errors").get(0).get("message").asText());
            }

            if (!lookupResp.has("message") || !lookupResp.get("message").asText().contains("User exists")) {
                throw new RuntimeException("User does not exist: " + lookupResp.get("message").asText());
            }

            String identifier = lookupResp.path("lookup").path("identifier").asText();
            String digest = lookupResp.path("lookup").path("digest").asText();

            if (identifier.isEmpty() || digest.isEmpty()) {
                throw new RuntimeException("Invalid lookup response, missing identifier or digest");
            }

            // -------- Step 2: Password Auth --------
            String loginUrl = String.format("%s/accounts/p/40-10002227248/signin/v2/primary/%s/password?digest=%s&cli_time=%d&servicename=ZohoCreator&service_language=en&serviceurl=%s/portal/academia-academic-services/redirectFromLogin", BASE_URL, identifier, digest, System.currentTimeMillis() / 1000, BASE_URL);

            HttpPost loginReq = new HttpPost(loginUrl);
            String body = String.format("{\"passwordauth\":{\"password\":\"%s\"}}", password);
            loginReq.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
            loginReq.setHeader("Accept", "application/json");
            loginReq.setHeader("Accept-Language", "en-US,en;q=0.9");
            loginReq.setHeader("sec-sh-ua", "\"Not(A:Brand\";v=\"99\", \"Google Chrome\";v=\"133\", \"Chromium\";v=\"133\"");
            loginReq.setHeader("sec-sh-ua-mobile", "?0");
            loginReq.setHeader("sec-sh-ua-platform", "'macOS'");
            loginReq.setHeader("sec-fetch-dest", "empty");
            loginReq.setHeader("sec-fetch-mode", "cors");
            loginReq.setHeader("sec-fetch-site", "same-origin");
            loginReq.setHeader("x-zcsrf-token", "iamcsrcoo=3c59613cb190a67effa5b17eaba832ef1eddaabeb7610c8c6a518b753bc73848b483b007a63f24d94d67d14dda0eca9f0c69e027c0ebd1bb395e51b2c6291d63");
            loginReq.setHeader("cookie", "npfwg=1; npf_r=; npf_l=www.srmist.edu.in; npf_u=https://www.srmist.edu.in/faculty/dr-g-y-rajaa-vikhram/; zalb_74c3a1eecc=44130d4069ebce16724b1740d9128cae; ZCNEWUIPUBLICPORTAL=true; zalb_f0e8db9d3d=93b1234ae1d3e88e54aa74d5fbaba677; stk=efbb3889860a8a5d4a9ad34903359b4e; zccpn=3c59613cb190a67effa5b17eaba832ef1eddaabeb7610c8c6a518b753bc73848b483b007a63f24d94d67d14dda0eca9f0c69e027c0ebd1bb395e51b2c6291d63; zalb_3309580ed5=2f3ce51134775cd955d0a3f00a177578; CT_CSRF_TOKEN=9d0ab1e6-9f71-40fd-826e-7229d199b64d; iamcsr=3c59613cb190a67effa5b17eaba832ef1eddaabeb7610c8c6a518b753bc73848b483b007a63f24d94d67d14dda0eca9f0c69e027c0ebd1bb395e51b2c6291d63; _zcsr_tmp=3c59613cb190a67effa5b17eaba832ef1eddaabeb7610c8c6a518b753bc73848b483b007a63f24d94d67d14dda0eca9f0c69e027c0ebd1bb395e51b2c6291d63; npf_fx=1; _ga_QNCRQG0GFE=GS1.1.1737645192.5.0.1737645194.58.0.0; TS014f04d9=0190f757c98d895868ec35d391f7090a39080dd8e7be840ed996d7e2827e600c5b646207bb76666e56e22bfaf8d2c06ec3c913fe80; cli_rgn=IN; JSESSIONID=E78E4C7013F0D931BD251EBA136D57AE; _ga=GA1.3.1900970259.1737341486; _gid=GA1.3.1348593805.1737687406; _gat=1; _ga_HQWPLLNMKY=GS1.3.1737687405.1.0.1737687405.0.0.0");
            loginReq.setHeader("Referer", (BASE_URL + "/accounts/p/10002227248/signin?hide_fp=true&servicename=ZohoCreator&service_language=en&css_url=/49910842/academia-academic-services/downloadPortalCustomCss/login&dcc=true&serviceurl=" + BASE_URL + "%2Fportal%2Facademia-academic-services%2FredirectFromLogin"));
            loginReq.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");
            loginReq.setEntity(new StringEntity(body, ContentType.APPLICATION_JSON));

            try (CloseableHttpResponse resp = client.execute(loginReq)) {
                int statusCode = resp.getCode();
                if (statusCode >= 400) {
                    Logger.getLogger("AcademiaService").warning("Login response: " + new String(resp.getEntity().getContent().readAllBytes()));
                    throw new RuntimeException("Login failed with status: " + statusCode);
                }
                // Optionally read response body
                String respText = new String(resp.getEntity().getContent().readAllBytes());
                if (respText.contains("error")) {
                    Logger.getLogger("AcademiaService").warning("Login err response: " + new String(resp.getEntity().getContent().readAllBytes()));
                    throw new RuntimeException("Login failed: " + respText);
                }
                StringBuilder cookie = new StringBuilder();
                for (var c : resp.getHeaders("Set-Cookie")) {
                    final String value = c.getValue();
                    // Each Set-Cookie looks like: "name=value; Path=/; HttpOnly; ..."
                    String[] parts = value.split(";", 2); // split only at first ';'
                    String nameValue = parts[0].trim();

                    // Ignore empty or deleted cookies
                    if (!nameValue.contains("=")) continue;
                    if (nameValue.endsWith("=")) continue;
                    if (value.toLowerCase().contains("expires=thu, 01 jan 1970")) continue;
                    if (value.toLowerCase().contains("max-age=0")) continue;

                    if (!cookie.isEmpty()) cookie.append("; ");
                    cookie.append(nameValue);
                }
                return cookie.toString();
            }
        }
    }

    public static String decodeHexEscapes(String input) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < input.length();) {
            if (i + 3 < input.length() && input.charAt(i) == '\\' && input.charAt(i+1) == 'x') {
                // parse the two hex digits after \x
                String hex = input.substring(i+2, i+4);
                try {
                    int code = Integer.parseInt(hex, 16);
                    out.append((char) code);
                    i += 4;
                    continue;
                } catch (NumberFormatException ignore) {
                    // fall through if not valid hex
                }
            }
            out.append(input.charAt(i));
            i++;
        }
        return out.toString();
    }

    public String extractAndDecode(String rawHtml) throws Exception {
        String[] parts = rawHtml.split("\\.sanitize\\('");
        if (parts.length < 2) {
            throw new Exception("Invalid timetable page format, .sanitize string not found");
        }

        String encoded = parts[1].split("'\\)")[0];
        return decodeHexEscapes(encoded);
    }

    public String fetchTimetablePage(String cookie) throws Exception {
        try (CloseableHttpClient client = HttpClients.custom().build()) {
            int year = Year.now().getValue();
            int startYear = year - 2;
            int endYear = year - 1;

            String url = String.format("%s/srm_university/academia-academic-services/page/My_Time_Table_%d_%02d", BASE_URL, startYear, (endYear % 100));
            HttpGet get = new HttpGet(url);
            get.addHeader(HttpHeaders.ACCEPT, "*/*");
            get.addHeader(HttpHeaders.ACCEPT_LANGUAGE, "en-US,en;q=0.9");
            get.addHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded; charset=UTF-8");
            get.addHeader("X-Requested-With", "XMLHttpRequest");
            get.addHeader(HttpHeaders.REFERER, BASE_URL);
            get.addHeader("Referrer-Policy", "strict-origin-when-cross-origin");
            get.addHeader(HttpHeaders.CACHE_CONTROL, "private, max-age=120, must-revalidate");
            get.addHeader(HttpHeaders.COOKIE, cookie);
            get.addHeader("Sec-Fetch-Dest", "empty");
            get.addHeader("Sec-Fetch-Mode", "cors");
            get.addHeader("Sec-Fetch-Site", "same-origin");

            var resp = client.execute(get);
            int code = resp.getCode();
            if (code >= 400) throw new RuntimeException("Timetable GET failed: " + code);

            final String s = new String(resp.getEntity().getContent().readAllBytes(), StandardCharsets.UTF_8);
            return extractAndDecode(s);
        }
    }

    /**
     * Parse student details + courses (Jsoup)
     */
    public StudentDetails parseStudentDetails(String html) {
        Document doc = Jsoup.parse(html);

        String regNumber = null, name = null, batch = null, mobile = null, department = null, semester = null;

        Element infoTable = doc.selectFirst("table[border=0][align=left]");
        if (infoTable != null) {
            Elements tds = infoTable.select("td");
            for (int i = 0; i < tds.size() - 1; i++) {
                String label = tds.get(i).text().trim();
                String val = tds.get(i + 1).text().trim();
                switch (label) {
                    case "Registration Number:" -> regNumber = val;
                    case "Name:" -> name = val;
                    case "Batch:" -> batch = val;
                    case "Mobile:" -> mobile = val;
                    case "Department:" -> department = val;
                    case "Semester:" -> semester = val;
                }
            }
        }

        Element table = doc.selectFirst("table.course_tbl");
        if (table == null) throw new RuntimeException("Course table not found in timetable HTML");

        List<Course> courses = new ArrayList<>();
        Elements tds = table.select("td");
        int columnsPerCourse = 11;

        for (int i = columnsPerCourse; i + columnsPerCourse <= tds.size(); i += columnsPerCourse) {
            List<String> vals = new ArrayList<>(columnsPerCourse);
            for (int j = 0; j < columnsPerCourse; j++) {
                vals.add(tds.get(i + j).text().trim());
            }
            Course c = new Course();
            c.setSNo(vals.get(0));
            c.setCourseCode(vals.get(1));
            c.setCourseTitle(vals.get(2));
            c.setCredit(vals.get(3));
            c.setRegnType(vals.get(4));
            c.setCategory(vals.get(5));
            c.setCourseType(vals.get(6));
            c.setFacultyName(vals.get(7));
            c.setSlot(vals.get(8));
            c.setRoomNo(vals.get(9));
            c.setAcademicYear(vals.get(10));
            courses.add(c);
        }

        StudentDetails sd = new StudentDetails();
        sd.setRegNumber(regNumber);
        sd.setName(name);
        sd.setBatch(batch == null ? 0 : Integer.parseInt(batch));
        sd.setMobile(mobile);
        sd.setDepartment(department);
        sd.setSemester(semester == null ? 0 : Integer.parseInt(semester));
        sd.setCourses(courses);
        return sd;
    }

    /**
     * Build structured timetable (Batch 1/2)
     */
    public List<TimetableDay> buildTimetable(StudentDetails sd) {
        // batches
        Map<Integer, List<List<String>>> batches = new HashMap<>();
        batches.put(1, List.of(List.of("A", "A", "F", "F", "G", "P6", "P7", "P8", "P9", "P10"), List.of("P11", "P12", "P13", "P14", "P15", "B", "B", "G", "G", "A"), List.of("C", "C", "A", "D", "B", "P26", "P27", "P28", "P29", "P30"), List.of("P31", "P32", "P33", "P34", "P35", "D", "D", "B", "E", "C"), List.of("E", "E", "C", "F", "D", "P46", "P47", "P48", "P49", "P50")));
        batches.put(2, List.of(List.of("P1", "P2", "P3", "P4", "P5", "A", "A", "F", "F", "G"), List.of("B", "B", "G", "G", "A", "P16", "P17", "P18", "P19", "P20"), List.of("P21", "P22", "P23", "P24", "P25", "C", "C", "A", "D", "B"), List.of("D", "D", "B", "E", "C", "P36", "P37", "P38", "P39", "P40"), List.of("P41", "P42", "P43", "P44", "P45", "E", "E", "C", "F", "D")));

        String[][] slotTimes = new String[][]{{"08:00", "08:50"}, {"08:50", "09:40"}, {"09:45", "10:35"}, {"10:40", "11:30"}, {"11:35", "12:25"}, {"12:30", "01:20"}, {"01:25", "02:15"}, {"02:20", "03:10"}, {"03:10", "04:00"}, {"04:00", "04:50"}};

        // slot -> course summary
        Map<String, Course> slotToCourse = new HashMap<>();
        for (Course c : sd.getCourses()) {
            if (c.getSlot() == null) continue;
            for (String s : c.getSlot().split("-")) {
                String slot = s.trim();
                if (!slot.isEmpty()) slotToCourse.put(slot, c);
            }
        }

        List<TimetableDay> result = new ArrayList<>();
        List<List<String>> batchSlots = batches.getOrDefault(sd.getBatch(), batches.get(1));

        for (int dayIdx = 0; dayIdx < batchSlots.size(); dayIdx++) {
            List<String> slots = batchSlots.get(dayIdx);
            List<TimetableEntry> entries = new ArrayList<>();
            for (int i = 0; i < slots.size(); i++) {
                String slot = slots.get(i);
                Course c = slotToCourse.get(slot);
                TimetableEntry e = new TimetableEntry();
                e.setSlot(slot);
                e.setStartTime(slotTimes[i][0]);
                e.setEndTime(slotTimes[i][1]);
                if (c != null) {
                    e.setCourseCode(c.getCourseCode());
                    e.setCourseType(c.getCourseType());
                    e.setTitle(c.getCourseTitle());
                    e.setFaculty(c.getFacultyName());
                    e.setRoom(c.getRoomNo());
                }
                entries.add(e);
            }
            TimetableDay day = new TimetableDay();
            day.setDayOrder(dayIdx + 1);
            day.setSchedule(entries);
            result.add(day);
        }

        return result;
    }

    /**
     * Fetch calendar HTML by current year and term
     */
    public String fetchCalendarHtml(String cookie, int semester) throws Exception {
        try (CloseableHttpClient client = HttpClients.custom().build()) {

            int year = Year.now().getValue();
            boolean isOdd = semester % 2 == 1;
            String term = isOdd ? "ODD" : "EVEN";
            String url = String.format("%s/srm_university/academia-academic-services/page/Academic_Planner_%d_%02d_%s", BASE_URL, year, (year + 1) % 100, term);

            HttpGet get = new HttpGet(url);
            get.addHeader(HttpHeaders.ACCEPT, "*/*");
            get.addHeader(HttpHeaders.COOKIE, cookie);
            get.addHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded; charset=UTF-8");
            var resp = client.execute(get);
            int code = resp.getCode();
            if (code >= 400) throw new RuntimeException("Calendar GET failed: " + code);

            // decode HTML entities like Python's html.unescape
            String raw = new String(resp.getEntity().getContent().readAllBytes(), StandardCharsets.UTF_8);
            return StringEscapeUtils.unescapeHtml4(raw);
        }
    }

    /**
     * Parse calendar events into month -> List<CalendarEvent> (month is 1..12)
     */
    public Map<Integer, List<CalendarEvent>> parseCalendarEvents(String html) {
        Document doc = Jsoup.parse(html);

        // Build month map from <th> like "Jul '25"
        Map<Integer, Integer> monthMap = extractMonthMap(doc);
        int columnsPerMonth = 5;
        Map<Integer, List<CalendarEvent>> map = new LinkedHashMap<>();
        monthMap.values().forEach(m -> map.put(m, new ArrayList<>()));

        Element table = doc.selectFirst("table");
        if (table == null) throw new RuntimeException("No <table> found in calendar HTML");

        Elements allTds = table.select("td");
        int months = monthMap.size();
        int rowWidth = months * columnsPerMonth;

        for (int i = 0; i < allTds.size(); i += rowWidth) {
            for (Map.Entry<Integer, Integer> e : monthMap.entrySet()) {
                int mIndex = e.getKey();     // group index
                int monthNum = e.getValue(); // 1..12

                int base = i + mIndex * columnsPerMonth;
                if (base + 3 >= allTds.size()) continue;

                String date = safeText(allTds, base);
                String day = safeText(allTds, base + 1);
                String event = safeText(allTds, base + 2);
                String doStr = safeText(allTds, base + 3);

                if (date == null || doStr == null || "-".equals(doStr) || !doStr.matches("\\d+")) continue;
                if (!date.matches("\\d+")) continue;

                int d = Integer.parseInt(date);
                int doCount = Integer.parseInt(doStr);
                String iso = String.format("%d-%02d-%02d", Year.now().getValue(), monthNum, d);

                CalendarEvent ce = new CalendarEvent();
                ce.setDate(iso);
                ce.setDay(day == null ? "" : day);
                ce.setEvent(event == null ? "" : event);
                ce.setDoCount(doCount);
                map.get(monthNum).add(ce);
            }
        }
        return map;
    }

    private String safeText(Elements tds, int idx) {
        if (idx < 0 || idx >= tds.size()) return null;
        return tds.get(idx).text().trim();
    }

    /**
     * Extract month headers -> (column-group index -> monthNumber)
     */
    private Map<Integer, Integer> extractMonthMap(Document doc) {
        Map<Integer, Integer> map = new LinkedHashMap<>();
        Elements ths = doc.select("th");

        int currentYY = Year.now().getValue() % 100;
        Pattern p = Pattern.compile("([A-Za-z]{3})\\s*'?" + currentYY);

        Map<String, Integer> mon = Map.ofEntries(Map.entry("Jan", 1), Map.entry("Feb", 2), Map.entry("Mar", 3), Map.entry("Apr", 4), Map.entry("May", 5), Map.entry("Jun", 6), Map.entry("Jul", 7), Map.entry("Aug", 8), Map.entry("Sep", 9), Map.entry("Oct", 10), Map.entry("Nov", 11), Map.entry("Dec", 12));

        Set<String> seen = new HashSet<>();
        for (int i = 0; i < ths.size(); i++) {
            String text = ths.get(i).text().trim();
            Matcher m = p.matcher(text);
            if (m.find()) {
                String abbr = capitalize3(m.group(1));
                if (mon.containsKey(abbr) && !seen.contains(abbr)) {
                    int monthIndex = i / 5; // each month has 5 columns
                    map.put(monthIndex, mon.get(abbr));
                    seen.add(abbr);
                }
            }
        }
        return map;
    }

    private String capitalize3(String s) {
        if (s == null || s.isEmpty()) return s;
        String t = s.toLowerCase(Locale.ROOT);
        return t.substring(0, 1).toUpperCase(Locale.ROOT) + t.substring(1, Math.min(3, t.length()));
    }

    /**
     * Utility: save any object to JSON file (like your save_to_file)
     */
    public void saveJsonToFile(Object obj, String fileName) throws Exception {
        var wrapper = Map.of("data", obj);
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(fileName), wrapper);
    }
}
