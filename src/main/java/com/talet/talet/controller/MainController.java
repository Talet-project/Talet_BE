package com.talet.talet.controller;

import com.talet.talet.dto.AddBookRequestDTO;
import com.talet.talet.service.FairyTaleBookService;
import com.talet.talet.util.TaletApiResponse;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Controller
@Slf4j
@RequiredArgsConstructor
public class MainController {

    private final FairyTaleBookService fairyTaleBookService;

    @Hidden
    @GetMapping("/admin/book/add")
    public String addBook() {
        return "BookAdd";
    }

    @Hidden
    @GetMapping("/admin/login")
    public String login() {
        return "Login";
    }

}
