package com.ooo.its.service;
import com.ooo.its.entity.Folder;
import com.ooo.its.repository.FolderRep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class FolderService {

    @Autowired
    private FolderRep folderRep;
    public List<Folder> getFolderList(){
        return folderRep.findAll();
    }
}
