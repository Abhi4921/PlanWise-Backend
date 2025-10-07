// src/main/java/com/srmist/academia/com.srmist.academia.controller/AcademiaDataController.java
package com.srmist.academia.controller;

import com.srmist.academia.dto.*;
import com.srmist.academia.model.User;
import com.srmist.academia.repository.UserRepository;
import com.srmist.academia.service.AcademiaService;
import com.srmist.academia.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/academia")
public class AcademiaDataController {
/*
zalb_f0e8db9d3d=7ad3232c36fdd9cc324fb86c2c0a58ad; Path=/; Secure; HttpOnly
wms-tkp-token_client_10002227248=; Max-Age=0; Expires=Thu, 01 Jan 1970 00:00:10 GMT; Domain=academia.srmist.edu.in; Path=/
wms-tkp-token_client_10002227248=; Max-Age=0; Expires=Thu, 01 Jan 1970 00:00:10 GMT; Domain=srmist.edu.in; Path=/
wms-tkp-token_client_10002227248=; Max-Age=0; Expires=Thu, 01 Jan 1970 00:00:10 GMT; Domain=edu.in; Path=/
wms-tkp-token_client_10002227248=; Max-Age=0; Expires=Thu, 01 Jan 1970 00:00:10 GMT; Path=/
_iamadt_client_10002227248=3ab9fbae174e86ff622fcd9e9d917b81fde9b174d4bc7e4ad62379a56d1703c98036c564db656a7391986d5e962e3488; Max-Age=3024000; Expires=Wed, 25-Feb-1970 07:54:37 GMT; HttpOnly; Domain=academia.srmist.edu.in; Path=/; Secure; SameSite=None;priority=High
_iambdt_client_10002227248=3354beb2d3c0b89fb8bab74d3acbb8a938d5a77325b04702ca2fb0317365582b73f65edfd8d7c47f4d89a6cb6cb8acf8a6d3c801ab33007937bac51da51bcbc9; Max-Age=3024000; Expires=Wed, 25-Feb-1970 07:54:37 GMT; HttpOnly; Domain=academia.srmist.edu.in; Path=/; Secure; SameSite=None;priority=High
_z_identity=true; Max-Age=7200; Expires=Wed, 21-Jan-1970 09:54:37 GMT; Path=/; Secure; SameSite=None;priority=Medium
*/
/*
zalb_f0e8db9d3d=7ad3232c36fdd9cc324fb86c2c0a58ad;
_iamadt_client_10002227248=ed4a87a977d6c99ede4a56962289615cb7aba004944cef6170d185929ad59f248cd606ae2f7fb3f3fc46567bbfeff3a0;
_iambdt_client_10002227248=85886433c19f1fc1b455ee0759b958137b75e75ba37ffb984b54bb6d04d6a0ae46ac0e9c21b5953c51b3399b5cdf4076e2932ae056d5c9a3affec27e0d536687
*/

    @Autowired
    private UserRepository userRepository;

    private final AcademiaService academiaService;
    public AcademiaDataController(AcademiaService academiaService) {
        this.academiaService = academiaService;
    }

    /** Pulls timetable page, parses details, builds timetable */
    @GetMapping("/timetable")
    public ResponseEntity<?> getTimetable(Authentication authentication) {
        try {

            String email = authentication.getName();
            User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
            String timetableHtml = academiaService.fetchTimetablePage(user.getCookies());
            StudentDetails sd = academiaService.parseStudentDetails(timetableHtml);
            var timetable = academiaService.buildTimetable(sd);

            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("studentDetails", sd);
            payload.put("timetable", timetable);

            return ResponseEntity.ok(Map.of("data", payload));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /** Pulls academic planner and parses working days/events */
    @GetMapping("/calendar")
    public ResponseEntity<?> getCalendar(Authentication authentication) {
        try {
            String email = authentication.getName();
            User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
            String timetableHtml = academiaService.fetchTimetablePage(user.getCookies());
            StudentDetails sd = academiaService.parseStudentDetails(timetableHtml);

            String calendarHtml = academiaService.fetchCalendarHtml(user.getCookies(), sd.getSemester());
            Map<Integer, List<CalendarEvent>> eventsByMonth = academiaService.parseCalendarEvents(calendarHtml);

            return ResponseEntity.ok(Map.of("data", eventsByMonth));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
