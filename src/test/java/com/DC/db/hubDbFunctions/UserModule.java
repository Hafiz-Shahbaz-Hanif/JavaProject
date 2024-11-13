package com.DC.db.hubDbFunctions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UserModule {

    private String userId;
    private String moduleId;
    private String name;
    private boolean canCreate;
    private boolean canRead;
    private boolean canUpdate;
    private boolean canDelete;

    public UserModule(String user_id, String module_id, String name, boolean can_create, boolean can_read, boolean can_update, boolean can_delete) {
        this.userId = user_id;
        this.moduleId = module_id;
        this.name = name;
        this.canCreate = can_create;
        this.canRead = can_read;
        this.canUpdate = can_update;
        this.canDelete = can_delete;
    }

    public String getModuleId() {
        return moduleId;
    }

    public List<String> getPrivileges(){
        List<String> privilegesList = new ArrayList<>();
        if (canCreate) {
            privilegesList.add("CREATE");
        }
        if (canRead) {
            privilegesList.add("READ");
        }
        if (canUpdate) {
            privilegesList.add("UPDATE");
        }
        if (canDelete) {
            privilegesList.add("DELETE");
        }
        Collections.sort(privilegesList);
        return privilegesList;
    }
}
