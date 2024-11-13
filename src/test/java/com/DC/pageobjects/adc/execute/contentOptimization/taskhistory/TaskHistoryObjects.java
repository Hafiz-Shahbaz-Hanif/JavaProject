package com.DC.pageobjects.adc.execute.contentOptimization.taskhistory;

import com.DC.utilities.DiffUtility;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class TaskHistoryObjects {
    public static class GeneralFeedbackBox {
        public String sender;
        public String timestamp;
        public String feedback;

        public GeneralFeedbackBox(String sender, String timestamp, String feedback) {
            this.sender = sender;
            this.timestamp = timestamp;
            this.feedback = feedback;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof GeneralFeedbackBox)) return false;
            GeneralFeedbackBox that = (GeneralFeedbackBox) o;
            return Objects.equals(sender, that.sender) && Objects.equals(timestamp, that.timestamp) && Objects.equals(feedback, that.feedback);
        }

        @Override
        public int hashCode() {
            return Objects.hash(sender, timestamp, feedback);
        }

        @Override
        public String toString() {
            return "{" +
                    "sender='" + sender + '\'' +
                    ", timestamp='" + timestamp + '\'' +
                    ", feedback='" + feedback + '\'' +
                    '}';
        }
    }

    public static class TaskHistoryDetailsData {
        public String timestamp;
        public String assignmentName;
        public String taskTitle;
        public String submittedBy;
        public List<String> products;
        public List<GeneralFeedbackBox> feedbackBoxes;

        public TaskHistoryDetailsData(String timestamp, String assignmentName, String taskTitle, String submittedBy, List<String> products, List<GeneralFeedbackBox> feedbackBoxes) {
            this.timestamp = timestamp;
            this.assignmentName = assignmentName;
            this.taskTitle = taskTitle;
            this.submittedBy = submittedBy;
            this.products = products;
            this.feedbackBoxes = feedbackBoxes;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof TaskHistoryDetailsData)) return false;
            TaskHistoryDetailsData that = (TaskHistoryDetailsData) o;
            return Objects.equals(timestamp, that.timestamp) && Objects.equals(assignmentName, that.assignmentName) && Objects.equals(taskTitle, that.taskTitle) && Objects.equals(submittedBy, that.submittedBy) && Objects.equals(products, that.products) && Objects.equals(feedbackBoxes, that.feedbackBoxes);
        }

        @Override
        public int hashCode() {
            return Objects.hash(timestamp, assignmentName, taskTitle, submittedBy, products, feedbackBoxes);
        }

        @Override
        public String toString() {
            return "{" +
                    "timestamp='" + timestamp + '\'' +
                    ", assignmentName='" + assignmentName + '\'' +
                    ", taskTitle='" + taskTitle + '\'' +
                    ", submittedBy='" + submittedBy + '\'' +
                    ", products=" + products +
                    ", feedbackBoxes=" + feedbackBoxes +
                    '}';
        }
    }

    public static class TaskDetailsProperties {
        public DiffUtility.Diff propertyId;
        public DiffUtility.Diff oldPropertyValue;
        public DiffUtility.Diff newPropertyValue;
        public LinkedList<DiffUtility.Diff> differenceColumn;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof TaskDetailsProperties)) return false;
            TaskDetailsProperties that = (TaskDetailsProperties) o;
            return Objects.equals(propertyId, that.propertyId) && Objects.equals(oldPropertyValue, that.oldPropertyValue) && Objects.equals(newPropertyValue, that.newPropertyValue) && Objects.equals(differenceColumn, that.differenceColumn);
        }

        @Override
        public int hashCode() {
            return Objects.hash(propertyId, oldPropertyValue, newPropertyValue, differenceColumn);
        }

        public TaskDetailsProperties(DiffUtility.Diff propertyId, DiffUtility.Diff oldPropertyValue, DiffUtility.Diff newPropertyValue, LinkedList<DiffUtility.Diff> differenceColumn) {
            this.propertyId = propertyId;
            this.oldPropertyValue = oldPropertyValue;
            this.newPropertyValue = newPropertyValue;
            this.differenceColumn = differenceColumn;
        }

        public TaskDetailsProperties() {
        }

        @Override
        public String toString() {
            return "{" +
                    "propertyId=" + propertyId +
                    ", oldPropertyValue=" + oldPropertyValue +
                    ", newPropertyValue=" + newPropertyValue +
                    ", differenceColumn=" + differenceColumn +
                    '}';
        }
    }
}
