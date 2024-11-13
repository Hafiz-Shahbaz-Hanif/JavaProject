package com.DC.db.hubDbFunctions;

public class HubQueries {


    public static String userRole = "select tu.email, tlpu.user_id, coalesce(CAST(ta.serialized_value AS text)) as serialized " +
            "from fwdb.global.t_user tu join fwdb.global.t_legacy_platform_user tlpu " +
            "on tu.id = tlpu.user_id join fwdb.global.t_aggregation ta " +
            "on ta.owner_id = tlpu.user_id " +
            "where tu.email = ?" +
            "group by email, user_id, serialized";


    public static String organizationsForUser = "SELECT _t_organization.name orgName, _t_organization.id orgId FROM global.t_user _user\n" +
            "JOIN global.t_business_unit_provision_user _business_unit_provision_user\n" +
            "ON _user.id = _business_unit_provision_user.user_id\n" +
            "JOIN global.t_business_unit_provision _business_unit_provision\n" +
            "ON _business_unit_provision_user.business_unit_provision_id = _business_unit_provision.id\n" +
            "JOIN global.t_business_unit _business_unit\n" +
            "ON _business_unit_provision.business_unit_id = _business_unit.id\n" +
            "join global.t_organization _t_organization\n" +
            "on _business_unit.organization_id  = _t_organization.id \n" +
            "JOIN global.t_retailer_platform_scope _retailer_platform_scope\n" +
            "ON _business_unit_provision.retailer_platform_scope_id = _retailer_platform_scope.id\n" +
            "JOIN global.t_retailer_platform _retailer_platform\n" +
            "ON _retailer_platform_scope.retailer_platform_id = _retailer_platform.id\n" +
            "JOIN global.t_retailer _retailer \n" +
            "ON _retailer_platform.retailer_id = _retailer.id\n" +
            "WHERE _user.auth0_id = ?\n" +
            "AND _user.deleted = false\n" +
            "AND _t_organization.deleted = false\n" +
            "AND _business_unit_provision_user.deleted = false\n" +
            "AND _business_unit_provision.deleted = false\n" +
            "AND _business_unit.deleted = false\n" +
            "AND _retailer_platform_scope.deleted = false\n" +
            "AND _retailer_platform.deleted = false\n" +
            "AND _retailer.deleted = false\n" +
            "group by orgName, orgId";

    public static String businessUnitsForUser = "SELECT _business_unit.name businessUnitName, _business_unit.id businessUnitId FROM global.t_user _user\n" +
            "JOIN global.t_business_unit_provision_user _business_unit_provision_user\n" +
            "ON _user.id = _business_unit_provision_user.user_id\n" +
            "JOIN global.t_business_unit_provision _business_unit_provision\n" +
            "ON _business_unit_provision_user.business_unit_provision_id = _business_unit_provision.id\n" +
            "JOIN global.t_business_unit _business_unit\n" +
            "ON _business_unit_provision.business_unit_id = _business_unit.id\n" +
            "join global.t_organization _t_organization\n" +
            "on _business_unit.organization_id  = _t_organization.id \n" +
            "JOIN global.t_retailer_platform_scope _retailer_platform_scope\n" +
            "ON _business_unit_provision.retailer_platform_scope_id = _retailer_platform_scope.id\n" +
            "JOIN global.t_retailer_platform _retailer_platform\n" +
            "ON _retailer_platform_scope.retailer_platform_id = _retailer_platform.id\n" +
            "JOIN global.t_retailer _retailer \n" +
            "ON _retailer_platform.retailer_id = _retailer.id\n" +
            "WHERE _user.auth0_id = ?\n" +
            "and _t_organization.id = uuid(?)\n" +
            "AND _user.deleted = false\n" +
            "AND _t_organization.deleted = false\n" +
            "AND _business_unit_provision_user.deleted = false\n" +
            "AND _business_unit_provision.deleted = false\n" +
            "AND _business_unit.deleted = false\n" +
            "AND _retailer_platform_scope.deleted = false\n" +
            "AND _retailer_platform.deleted = false\n" +
            "AND _retailer.deleted = false\n" +
            "group by businessUnitName, businessUnitId";

    public static String businessUnitsForAuthorizeUser = "SELECT _business_unit.name businessUnitName, _business_unit.id businessUnitId FROM global.t_user _user\n" +
            "JOIN global.t_business_unit_provision_user _business_unit_provision_user\n" +
            "ON _user.id = _business_unit_provision_user.user_id\n" +
            "JOIN global.t_business_unit_provision _business_unit_provision\n" +
            "ON _business_unit_provision_user.business_unit_provision_id = _business_unit_provision.id\n" +
            "JOIN global.t_business_unit _business_unit\n" +
            "ON _business_unit_provision.business_unit_id = _business_unit.id\n" +
            "join global.t_organization _t_organization\n" +
            "on _business_unit.organization_id  = _t_organization.id \n" +
            "JOIN global.t_retailer_platform_scope _retailer_platform_scope\n" +
            "ON _business_unit_provision.retailer_platform_scope_id = _retailer_platform_scope.id\n" +
            "JOIN global.t_retailer_platform _retailer_platform\n" +
            "ON _retailer_platform_scope.retailer_platform_id = _retailer_platform.id\n" +
            "JOIN global.t_retailer _retailer \n" +
            "ON _retailer_platform.retailer_id = _retailer.id\n" +
            "WHERE _user.auth0_id = ?\n" +
            "AND _user.deleted = false\n" +
            "AND _t_organization.deleted = false\n" +
            "AND _business_unit_provision_user.deleted = false\n" +
            "AND _business_unit_provision.deleted = false\n" +
            "AND _business_unit.deleted = false\n" +
            "AND _retailer_platform_scope.deleted = false\n" +
            "AND _retailer_platform.deleted = false\n" +
            "AND _retailer.deleted = false\n" +
            "group by businessUnitName, businessUnitId";

    public static String retailerPlatformsForUser = "SELECT _retailer_platform.domain platformDomain, _retailer_platform.id platformId FROM global.t_user _user\n" +
            "JOIN global.t_business_unit_provision_user _business_unit_provision_user\n" +
            "ON _user.id = _business_unit_provision_user.user_id\n" +
            "JOIN global.t_business_unit_provision _business_unit_provision\n" +
            "ON _business_unit_provision_user.business_unit_provision_id = _business_unit_provision.id\n" +
            "JOIN global.t_business_unit _business_unit\n" +
            "ON _business_unit_provision.business_unit_id = _business_unit.id\n" +
            "join global.t_organization _t_organization\n" +
            "on _business_unit.organization_id  = _t_organization.id \n" +
            "JOIN global.t_retailer_platform_scope _retailer_platform_scope\n" +
            "ON _business_unit_provision.retailer_platform_scope_id = _retailer_platform_scope.id\n" +
            "JOIN global.t_retailer_platform _retailer_platform\n" +
            "ON _retailer_platform_scope.retailer_platform_id = _retailer_platform.id\n" +
            "JOIN global.t_retailer _retailer \n" +
            "ON _retailer_platform.retailer_id = _retailer.id\n" +
            "WHERE _user.auth0_id = ? \n" +
            "and _business_unit.id = uuid(?)\n" +
            "AND _user.deleted = false\n" +
            "AND _t_organization.deleted = false\n" +
            "AND _business_unit_provision_user.deleted = false\n" +
            "AND _business_unit_provision.deleted = false\n" +
            "AND _business_unit.deleted = false\n" +
            "AND _retailer_platform_scope.deleted = false\n" +
            "AND _retailer_platform.deleted = false\n" +
            "AND _retailer.deleted = false\n" +
            "group by platformDomain, platformId";

    public static String buRetailersForUser = "SELECT _retailer.name retailerName, _retailer.id retailerId FROM global.t_user _user\n" +
            "JOIN global.t_business_unit_provision_user _business_unit_provision_user\n" +
            "ON _user.id = _business_unit_provision_user.user_id\n" +
            "JOIN global.t_business_unit_provision _business_unit_provision\n" +
            "ON _business_unit_provision_user.business_unit_provision_id = _business_unit_provision.id\n" +
            "JOIN global.t_business_unit _business_unit\n" +
            "ON _business_unit_provision.business_unit_id = _business_unit.id\n" +
            "join global.t_organization _t_organization\n" +
            "on _business_unit.organization_id  = _t_organization.id \n" +
            "JOIN global.t_retailer_platform_scope _retailer_platform_scope\n" +
            "ON _business_unit_provision.retailer_platform_scope_id = _retailer_platform_scope.id\n" +
            "JOIN global.t_retailer_platform _retailer_platform\n" +
            "ON _retailer_platform_scope.retailer_platform_id = _retailer_platform.id\n" +
            "JOIN global.t_retailer _retailer \n" +
            "ON _retailer_platform.retailer_id = _retailer.id\n" +
            "WHERE _user.auth0_id = ?\n" +
            "and _t_organization.id = uuid(?)\n" +
            "and _business_unit.id = uuid(?)\n" +
            "AND _user.deleted = false\n" +
            "AND _t_organization.deleted = false\n" +
            "AND _business_unit_provision_user.deleted = false\n" +
            "AND _business_unit_provision.deleted = false\n" +
            "AND _business_unit.deleted = false\n" +
            "AND _retailer_platform_scope.deleted = false\n" +
            "AND _retailer_platform.deleted = false\n" +
            "AND _retailer.deleted = false\n" +
            "group by retailerName, retailerId";

    public static String buRetailersForAuthorizeUser = "SELECT _retailer.name retailerName, _retailer.id retailerId FROM global.t_user _user\n" +
            "JOIN global.t_business_unit_provision_user _business_unit_provision_user\n" +
            "ON _user.id = _business_unit_provision_user.user_id\n" +
            "JOIN global.t_business_unit_provision _business_unit_provision\n" +
            "ON _business_unit_provision_user.business_unit_provision_id = _business_unit_provision.id\n" +
            "JOIN global.t_business_unit _business_unit\n" +
            "ON _business_unit_provision.business_unit_id = _business_unit.id\n" +
            "join global.t_organization _t_organization\n" +
            "on _business_unit.organization_id  = _t_organization.id \n" +
            "JOIN global.t_retailer_platform_scope _retailer_platform_scope\n" +
            "ON _business_unit_provision.retailer_platform_scope_id = _retailer_platform_scope.id\n" +
            "JOIN global.t_retailer_platform _retailer_platform\n" +
            "ON _retailer_platform_scope.retailer_platform_id = _retailer_platform.id\n" +
            "JOIN global.t_retailer _retailer \n" +
            "ON _retailer_platform.retailer_id = _retailer.id\n" +
            "WHERE _user.auth0_id = ?\n" +
            "and _business_unit.id = uuid(?)\n" +
            "AND _user.deleted = false\n" +
            "AND _t_organization.deleted = false\n" +
            "AND _business_unit_provision_user.deleted = false\n" +
            "AND _business_unit_provision.deleted = false\n" +
            "AND _business_unit.deleted = false\n" +
            "AND _retailer_platform_scope.deleted = false\n" +
            "AND _retailer_platform.deleted = false\n" +
            "AND _retailer.deleted = false\n" +
            "group by retailerName, retailerId";

    public static String buRetailerPlatformsForUser = "SELECT _retailer_platform.domain platformDomain, _retailer_platform.id platformId FROM global.t_user _user\n" +
            "JOIN global.t_business_unit_provision_user _business_unit_provision_user\n" +
            "ON _user.id = _business_unit_provision_user.user_id\n" +
            "JOIN global.t_business_unit_provision _business_unit_provision\n" +
            "ON _business_unit_provision_user.business_unit_provision_id = _business_unit_provision.id\n" +
            "JOIN global.t_business_unit _business_unit\n" +
            "ON _business_unit_provision.business_unit_id = _business_unit.id\n" +
            "join global.t_organization _t_organization\n" +
            "on _business_unit.organization_id  = _t_organization.id \n" +
            "JOIN global.t_retailer_platform_scope _retailer_platform_scope\n" +
            "ON _business_unit_provision.retailer_platform_scope_id = _retailer_platform_scope.id\n" +
            "JOIN global.t_retailer_platform _retailer_platform\n" +
            "ON _retailer_platform_scope.retailer_platform_id = _retailer_platform.id\n" +
            "JOIN global.t_retailer _retailer \n" +
            "ON _retailer_platform.retailer_id = _retailer.id\n" +
            "WHERE _user.auth0_id = ?\n" +
            "and _t_organization.id = uuid(?)\n" +
            "and _business_unit.id = uuid(?)\n" +
            "and _retailer.id = uuid(?)\n" +
            "AND _user.deleted = false\n" +
            "AND _t_organization.deleted = false\n" +
            "AND _business_unit_provision_user.deleted = false\n" +
            "AND _business_unit_provision.deleted = false\n" +
            "AND _business_unit.deleted = false\n" +
            "AND _retailer_platform_scope.deleted = false\n" +
            "AND _retailer_platform.deleted = false\n" +
            "AND _retailer.deleted = false\n" +
            "group by platformDomain, platformId";

    public static String buRetailerPlatformsForAuthorizeUser = "SELECT _retailer_platform.domain platformDomain, _retailer_platform.id platformId FROM global.t_user _user\n" +
            "JOIN global.t_business_unit_provision_user _business_unit_provision_user\n" +
            "ON _user.id = _business_unit_provision_user.user_id\n" +
            "JOIN global.t_business_unit_provision _business_unit_provision\n" +
            "ON _business_unit_provision_user.business_unit_provision_id = _business_unit_provision.id\n" +
            "JOIN global.t_business_unit _business_unit\n" +
            "ON _business_unit_provision.business_unit_id = _business_unit.id\n" +
            "join global.t_organization _t_organization\n" +
            "on _business_unit.organization_id  = _t_organization.id \n" +
            "JOIN global.t_retailer_platform_scope _retailer_platform_scope\n" +
            "ON _business_unit_provision.retailer_platform_scope_id = _retailer_platform_scope.id\n" +
            "JOIN global.t_retailer_platform _retailer_platform\n" +
            "ON _retailer_platform_scope.retailer_platform_id = _retailer_platform.id\n" +
            "JOIN global.t_retailer _retailer \n" +
            "ON _retailer_platform.retailer_id = _retailer.id\n" +
            "WHERE _user.auth0_id = ?\n" +
            "and _business_unit.id = uuid(?)\n" +
            "and _retailer.id = uuid(?)\n" +
            "AND _user.deleted = false\n" +
            "AND _t_organization.deleted = false\n" +
            "AND _business_unit_provision_user.deleted = false\n" +
            "AND _business_unit_provision.deleted = false\n" +
            "AND _business_unit.deleted = false\n" +
            "AND _retailer_platform_scope.deleted = false\n" +
            "AND _retailer_platform.deleted = false\n" +
            "AND _retailer.deleted = false\n" +
            "group by platformDomain, platformId";

    public static String retailerPlatforms = "select * from global.t_retailer_platform trp \n" +
            "group by id";

    public static String moduleIdsForAuthorizeUser = "select tu.email, tu.auth0_id, tbu.name as bu_name, tbu.id bu_id, tm.name module_name, tm.id module_id from global.t_business_unit tbu \n" +
            "join global.t_business_unit_module tbum \n" +
            "on tbu.id = tbum.business_unit_id \n" +
            "join global.t_module tm \n" +
            "on tbum.module_id  = tm.id \n" +
            "join global.t_business_unit_module_user tbumu \n" +
            "on tbum.id  = tbumu.business_unit_module_id \n" +
            "join global.t_user tu \n" +
            "on tbumu.user_id = tu.id \n" +
            "where tu.auth0_id = ?\n" +
            "and tbu.id = uuid(?)\n" +
            "and tbu.deleted = false \n" +
            "and tm.deleted = false \n" +
            "and tu.deleted = false";

    public static String buAgnosticModuleUser = "select module_id  from global.t_business_unit_agnostic_module_user tbmu\n" +
            "join global.t_user tu \n" +
            "on tbmu.user_id = tu.id \n" +
            "where auth0_id = ?;";

    public static String userBuModuleAuthorization = "UPDATE global.t_business_unit_module_user\n" +
            "SET can_create  = ?, can_read = ?, can_update = ?, can_delete = ?\n" +
            "WHERE user_id = uuid(?)\n" +
            "AND business_unit_module_id = uuid(?);";

    public static String userBuModuleIds = "select tu.id user_id, tbum.id bu_module_id from global.t_business_unit tbu\n" +
            "join global.t_business_unit_module tbum\n" +
            "on tbu.id = tbum.business_unit_id\n" +
            "join global.t_module tm\n" +
            "on tbum.module_id  = tm.id\n" +
            "join global.t_business_unit_module_user tbumu\n" +
            "on tbum.id  = tbumu.business_unit_module_id\n" +
            "join global.t_user tu\n" +
            "on tbumu.user_id = tu.id\n" +
            "where tu.auth0_id = ?\n" +
            "and tbu.name = ?\n" +
            "and tm.name = ?\n" +
            "and tbu.deleted = false\n" +
            "and tm.deleted = false\n" +
            "and tu.deleted = false";

    public static String legacyPlatforms = "select * from global.t_legacy_platform";

    public static String aggregationType = "select * from global.t_aggregation_type \n" +
            "where id = uuid(?)";

    public static String createOrganization = "INSERT INTO global.t_organization (id, name, deleted)\n" +
            "VALUES (\n" +
            "uuid(?),\n" +
            "?,\n" +
            "?\n" +
            ");";

    public static String organizationAggregation = "select * from global.t_aggregation \n" +
            "where id = uuid(?)";

    public static String createUser = "INSERT INTO global.t_user (id, email, first_name, last_name, auth0_id, last_login, deleted, external)\n" +
            "    VALUES (\n" +
            "    gen_random_uuid(),\n" +
            "    ?,\n" +
            "    ?,\n" +
            "    ?,\n" +
            "    ?,\n" +
            "    null,\n" +
            "    false,\n" +
            "    false\n" +
            ");";

    public static String createLegacyPlatformForUser = "INSERT INTO global.t_legacy_platform_user (id, user_id, platform_id)\n" +
            "\tselect\n" +
            "\tgen_random_uuid(),\n" +
            "\t(select id from global.t_user tu where tu.email = ?),\n" +
            "(select id from global.t_legacy_platform lp where lp.platform_name = ?);";

    public static String deleteLegacyPlatformForUser = "delete from global.t_legacy_platform_user tlpu \n" +
            "where tlpu.user_id  = uuid(?)";

    public static String deleteUser = "delete from global.t_user tu \n" +
            "where tu.id = uuid(?)";

    public static String userId = "select * from global.t_user tu \n" +
            "where tu.email  = ?";

    public static String buId = "select * from global.t_business_unit\n" +
            "where name = ?";

    public static String goalMetricId = "select * from goal.t_metric tm \n" +
            "where tm.metric = ?";

    public static String pivotValueId = "select * from goal.t_business_unit_metric_goal tbumg \n" +
            "where tbumg.pivot_type = ?";

    public static String retailerId = "select trp.id retailer_platform_id from global.t_retailer_platform trp \n" +
            "join global.t_retailer tr \n" +
            "on trp.retailer_id = tr.id \n" +
            "join global.t_country tc \n" +
            "on trp.country_id = tc.id \n" +
            "where tc.name = ?\n" +
            "and tr.name = ?\n" +
            "and trp.domain = ?";

    public static String userIdByAuthId = "select id from global.t_user tu \n" +
            "where tu.auth0_id  = ?";

    public static String aggregation = "select ta.id aggregation_id, ta.aggregation_type_id, ta.owner_id, ta.foreign_id, tat.type_name, tat.legacy_platform_id, tlp.platform_name, ta.serialized_value from global.t_aggregation_type tat \n" +
            "  join global.t_legacy_platform tlp \n" +
            "  on tat.legacy_platform_id = tlp.id \n" +
            "  join global.t_aggregation ta \n" +
            "  on ta.aggregation_type_id = tat.id\n" +
            "  where tat.type_name = ?\n" +
            "  and ta.owner_id = uuid(?) \n" +
            "  and tlp.platform_name = ?";

    public static String userOrganizations = "SELECT \n" +
            "_t_organization.id organizationId,\n" +
            "_business_unit.id businessUnitId,\n" +
            "_retailer_platform.id retailerPlatformId\n" +
            "FROM global.t_user _user\n" +
            "JOIN global.t_business_unit_provision_user _business_unit_provision_user\n" +
            "ON _user.id = _business_unit_provision_user.user_id\n" +
            "JOIN global.t_business_unit_provision _business_unit_provision\n" +
            "ON _business_unit_provision_user.business_unit_provision_id = _business_unit_provision.id\n" +
            "JOIN global.t_business_unit _business_unit\n" +
            "ON _business_unit_provision.business_unit_id = _business_unit.id\n" +
            "join global.t_organization _t_organization\n" +
            "on _business_unit.organization_id  = _t_organization.id \n" +
            "JOIN global.t_retailer_platform_scope _retailer_platform_scope\n" +
            "ON _business_unit_provision.retailer_platform_scope_id = _retailer_platform_scope.id\n" +
            "JOIN global.t_retailer_platform _retailer_platform\n" +
            "ON _retailer_platform_scope.retailer_platform_id = _retailer_platform.id\n" +
            "JOIN global.t_retailer _retailer \n" +
            "ON _retailer_platform.retailer_id = _retailer.id\n" +
            "WHERE _user.auth0_id = ?\n" +
            "AND _user.deleted = false\n" +
            "and _t_organization.deleted = false\n" +
            "AND _business_unit_provision_user.deleted = false\n" +
            "AND _business_unit_provision.deleted = false\n" +
            "AND _business_unit.deleted = false\n" +
            "AND _retailer_platform_scope.deleted = false\n" +
            "AND _retailer_platform.deleted = false\n" +
            "AND _retailer.deleted = false\n" +
            "group by organizationId, businessUnitId, retailerPlatformId";

        public static String userBuAggregations = "select * from \n" +
                "\n" +
                "(\n" +
                "SELECT \n" +
                "\n" +
                "_user.id userId,\n" +
                "_user.auth0_id auth0_id,\n" +
                "_t_organization.name orgName, \n" +
                "_t_organization.id orgId,\n" +
                "_business_unit.name businessUnitName,\n" +
                "_business_unit.id businessUnitId,\n" +
                "_retailer.id retailerId,\n" +
                "_retailer.name retailerName,\n" +
                "_retailer_platform.id retailerPlatformId,\n" +
                "_retailer_platform.domain retailerPlatformDomain,\n" +
                "_business_unit_provision.id buProvisionId,\n" +
                "_business_unit_provision.retail_enabled,\n" +
                "_business_unit_provision.onsite_media_enabled,\n" +
                "_business_unit_provision.offsite_media_enabled,\n" +
                "_country.code, \n" +
                "_country.currency_code,\n" +
                "_country.currency_symbol,\n" +
                "_retailer_platform_scope.id retailerPlatformScopeId\n" +
                "\n" +
                "FROM global.t_user _user\n" +
                "JOIN global.t_business_unit_provision_user _business_unit_provision_user\n" +
                "ON _user.id = _business_unit_provision_user.user_id\n" +
                "JOIN global.t_business_unit_provision _business_unit_provision\n" +
                "ON _business_unit_provision_user.business_unit_provision_id = _business_unit_provision.id\n" +
                "JOIN global.t_business_unit _business_unit\n" +
                "ON _business_unit_provision.business_unit_id = _business_unit.id\n" +
                "join global.t_organization _t_organization\n" +
                "on _business_unit.organization_id  = _t_organization.id \n" +
                "JOIN global.t_retailer_platform_scope _retailer_platform_scope\n" +
                "ON _business_unit_provision.retailer_platform_scope_id = _retailer_platform_scope.id\n" +
                "JOIN global.t_retailer_platform _retailer_platform\n" +
                "ON _retailer_platform_scope.retailer_platform_id = _retailer_platform.id\n" +
                "JOIN global.t_retailer _retailer \n" +
                "ON _retailer_platform.retailer_id = _retailer.id\n" +
                "join global.t_country _country \n" +
                "on _country.id  = _retailer_platform.country_id \n" +
                "where _user.deleted = false\n" +
                "and _t_organization.deleted = false\n" +
                "AND _business_unit_provision_user.deleted = false\n" +
                "AND _business_unit_provision.deleted = false\n" +
                "AND _business_unit.deleted = false\n" +
                "AND _retailer_platform_scope.deleted = false\n" +
                "AND _retailer_platform.deleted = false\n" +
                "AND _retailer.deleted = false\n" +
                ") as q1\n" +
                "\n" +
                "left join (\n" +
                "\n" +
                "select ta.id aggregation_id, ta.aggregation_type_id, ta.owner_id, ta.foreign_id, tat.type_name, tat.legacy_platform_id, tlp.platform_name, ta.serialized_value from global.t_aggregation_type tat \n" +
                "join global.t_legacy_platform tlp \n" +
                "on tat.legacy_platform_id = tlp.id \n" +
                "join global.t_aggregation ta \n" +
                "on ta.aggregation_type_id = tat.id\n" +
                "where tat.type_name = 'business-unit'\n" +
                "\n" +
                ") as q2\n" +
                "\n" +
                "on q1.retailerPlatformId = q2.foreign_id\n" +
                "and q1.businessUnitId = q2.owner_id \n" +
                "WHERE q1.auth0_id = ?\n" +
                "and q1.orgId= uuid(?)\n" +
                "and q1.businessUnitId = uuid(?)\n" +
                "and q1.retailerPlatformId = uuid(?)";

        public static String userBuModule = "select tbmu.user_id, tbum.business_unit_id, tbum.module_id, tm.name, tbmu.can_create, tbmu.can_read, tbmu.can_update, tbmu.can_delete from  global.t_business_unit_module_user tbmu\n" +
                "join global.t_business_unit_module tbum \n" +
                "on tbmu.business_unit_module_id = tbum.id \n" +
                "join global.t_module tm \n" +
                "on tbum.module_id = tm.id\n" +
                "join global.t_user tu \n" +
                "on tu.id = tbmu.user_id\n" +
                "WHERE auth0_id = ?\n" +
                "and  tbum.business_unit_id = uuid(?)\n" +
                "and tbum.module_id = uuid(?)";

        public static String buAgnosticModule = "select tbmu.user_id, tbmu.module_id, tm.name, tbmu.can_create, tbmu.can_read, tbmu.can_update, tbmu.can_delete from global.t_business_unit_agnostic_module_user tbmu\n" +
            "join global.t_user tu \n" +
            "on tbmu.user_id = tu.id \n" +
            "join global.t_module tm \n" +
            "on tbmu.module_id = tm.id\n" +
            "where auth0_id = ?\n" +
            "and tbmu.module_id = uuid(?)";

    public static String connectUserScreenPermissions(int roleId, int userId) {
        return "UPDATE exec_dashboard.T_USER_ROLE tur " +
                "JOIN exec_dashboard.T_ROLE tr ON tr.ID = tur.ROLE_ID " +
                "JOIN exec_dashboard.T_ROLE_TYPE trt ON tr.ROLE_TYPE_ID = trt.ID " +
                "SET tur.ROLE_ID = " + roleId +
                " WHERE tur.USER_ID = " + userId +
                " AND trt.NAME = 'Module';";
    }

    public static String goalPivotDetails = "select trps.id retailer_platform_scope_id, tbup.id bu_provision_id, tbumg.pivot_type, tbumg.pivot_value_id pivot_value_id from global.t_organization torg\n" +
            "join global.t_retailer_platform_scope trps \n" +
            "on torg.id = trps.organization_id \n" +
            "join global.t_retailer_platform trp \n" +
            "on trps.retailer_platform_id = trp.id \n" +
            "join global.t_retailer tr\n" +
            "on trp.retailer_id = tr.id \n" +
            "join global.t_business_unit_provision tbup \n" +
            "on trps.id = tbup.retailer_platform_scope_id \n" +
            "join global.t_business_unit bu\n" +
            "on tbup.business_unit_id = bu.id \n" +
            "join goal.t_business_unit_metric_goal tbumg \n" +
            "on tbumg.business_unit_provision_id = tbup.id\n" +
            "where bu.name = ?\n" +
            "and tr.name = ?\n" +
            "and trp.domain = ?\n" +
            "and tbumg.pivot_type = ?\n" +
            "limit 1";



}

