package com.DC.db.analyze;

public class GoalsHubQueries {

    public static String goalId = "select * from goal.t_metric_goal\n" +
            "where id  = uuid(?)";

    public static String goal = "select * from goal.t_metric_goal limit 1";

    public static String goalsHubMetrics = "select * from goal.t_metric\n" +
            "where id = uuid(?)";

    public static String goalMetricDetails = "select tmg.id metric_goal_id, tm.goal_type, tm.metric_type,\n" +
            "tmds.metric_data_source, tmds.metric_data_source_type, tbumg.pivot_type, tbumg.pivot_value_id,\n" +
            "tgc.current_value, tmg.goal_specification from goal.t_metric_goal tmg\n" +
            "join goal.t_metric tm \n" +
            "on tmg.metric_id = tm.id \n" +
            "join goal.t_metric_data_source tmds \n" +
            "on tm.metric_data_source_id = tmds.id \n" +
            "join goal.t_business_unit_metric_goal tbumg \n" +
            "on tbumg.metric_goal_id = tmg.id\n" +
            "join goal.t_goal_calculation tgc \n" +
            "on tmg.id = tgc.metric_goal_id \n" +
            "where tbumg.metric_goal_id = uuid(?)";

    public static String goalsHubMetricGoal = "select * from goal.t_metric_goal\n" +
            "where id = uuid(?)";

    public static String goalsHubGoalCalculation = "select * from goal.t_goal_calculation\n" +
            "where metric_goal_id = uuid(?)";
}
