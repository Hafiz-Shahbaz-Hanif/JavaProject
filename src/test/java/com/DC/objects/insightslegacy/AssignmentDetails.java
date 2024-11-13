package com.DC.objects.insightslegacy;

import com.DC.utilities.enums.Enums;
import java.util.Objects;

public class AssignmentDetails extends AssingmentDetailsBasicSettings{

    public AssingmentDetailsBasicSettings assignmentDetailsBasicSettings;
    public Enums.TaskStatus requiredStatus;
    public Enums.TaskType taskType;
    public String jwt;
    public String[] expectedProducts;

    public AssignmentDetails(Enums.TaskStatus requiredStatus, Enums.TaskType taskType, String jwt, String[] expectedProducts) {
        this.requiredStatus = requiredStatus;
        this.taskType = taskType;
        this.jwt = jwt;
        this.expectedProducts = expectedProducts;
        this.assignmentDetailsBasicSettings = assignmentDetailsBasicSettings;
    }

    public AssignmentDetails() {
    }

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (!(o instanceof AssignmentDetails)) return false;
        AssignmentDetails that = (AssignmentDetails) o;
        return requiredStatus == that.requiredStatus &&
                taskType == that.taskType &&
                jwt == that.jwt &&
                expectedProducts == that.expectedProducts &&
                assignmentDetailsBasicSettings == that.assignmentDetailsBasicSettings;
    }

    @Override
    public int hashCode() {
        return Objects.hash(requiredStatus, taskType, jwt, expectedProducts, assignmentDetailsBasicSettings);
    }

    @Override
    public String toString() {
        return "AssignmentDetails{" +
                ", requiredStatus='" + requiredStatus + '\'' +
                ", taskType='" + taskType + '\'' +
                ", jwt='" + jwt + '\'' +
                ", expectedProducts='" + expectedProducts + '\'' +
                ", assingmentDetailsBasicSettings='" + assignmentDetailsBasicSettings + '\'' +
                '}';
    }
}

class AssingmentDetailsBasicSettings {
    public String assignmentName;
    public String taskTitle;
    public AssingmentDetailsBasicSettings(String assignmentName, String taskTitle) {
        this.assignmentName = assignmentName;
        this.taskTitle = taskTitle;
    }

    public AssingmentDetailsBasicSettings() {
    }

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (!(o instanceof AssingmentDetailsBasicSettings)) return false;
        AssingmentDetailsBasicSettings that = (AssingmentDetailsBasicSettings) o;
        return assignmentName == that.assignmentName &&
                taskTitle == that.taskTitle;
    }

    @Override
    public int hashCode() {
    return Objects.hash(assignmentName, taskTitle);
    }

    @Override
    public String toString() {
        return "AssingmentDetailsBasicSettings{" +
                ", assignmentName='" + assignmentName + '\'' +
                ", taskTitle='" + taskTitle + '\'' +
                '}';
    }

}
