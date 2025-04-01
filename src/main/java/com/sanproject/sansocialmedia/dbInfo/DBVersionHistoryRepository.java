package com.sanproject.sansocialmedia.dbInfo;

import com.sanproject.sansocialmedia.entity.DBVersionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DBVersionHistoryRepository extends JpaRepository<DBVersionHistory, Long> {
    List<DBVersionHistory> findAll();
}
