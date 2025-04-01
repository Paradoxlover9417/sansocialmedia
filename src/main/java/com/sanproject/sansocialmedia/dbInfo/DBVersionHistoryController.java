package com.sanproject.sansocialmedia.dbInfo;

import com.sanproject.sansocialmedia.entity.DBVersionHistory;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class DBVersionHistoryController {

    private final DBVersionHistoryService dbVersionHistoryService;

    @GetMapping("/api/DBVersionHistory")
    public List<DBVersionHistory> getAllVersionHistory() {
        return dbVersionHistoryService.getAllVersionHistory();
    }
}
