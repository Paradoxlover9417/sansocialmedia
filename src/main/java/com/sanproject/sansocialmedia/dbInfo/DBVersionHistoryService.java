package com.sanproject.sansocialmedia.dbInfo;

import com.sanproject.sansocialmedia.entity.DBVersionHistory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class DBVersionHistoryService {

    private final DBVersionHistoryRepository dbVersionHistoryRepository;

    public List<DBVersionHistory> getAllVersionHistory() {
        return dbVersionHistoryRepository.findAll();
    }
}
