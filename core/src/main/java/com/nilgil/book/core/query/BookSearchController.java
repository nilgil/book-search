package com.nilgil.book.core.query;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Search", description = "검색 관련 엔드포인트 (향후 확장용)")
@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class BookSearchController {


}
