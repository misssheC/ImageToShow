package com.ooo.its.repository;

import com.ooo.its.entity.Folder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FolderRep extends JpaRepository<Folder,Long> {
    List<Folder> findAll();
}
