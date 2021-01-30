package it.ness.sample.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import it.ness.codebuilder.annotations.CodeBuilderDefaultOrderBy;
import it.ness.codebuilder.annotations.CodeBuilderFilterDef;
import it.ness.codebuilder.annotations.CodeBuilderOption;
import it.ness.codebuilder.annotations.CodeBuilderRsPath;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "all_options")
@CodeBuilderDefaultOrderBy(orderBy = "uuid asc")
@CodeBuilderRsPath(path = "ALL_OPTIONS")
public class AllOptions extends PanacheEntityBase {

    @Id
    public String uuid;

    @CodeBuilderFilterDef()
    public String simplename;

    @CodeBuilderFilterDef(type = "LocalDateTime")
    public LocalDateTime simpledatetime;

    @CodeBuilderFilterDef(type = "LocalDate")
    public LocalDate simpledate;

    @CodeBuilderFilterDef(options = {CodeBuilderOption.EXECUTE_ALWAYS})
    public boolean active;

    @CodeBuilderFilterDef(name = "customer_uuid", type = "string", condition = "not", options = {CodeBuilderOption.WITHOUT_PARAMETERS})
    public String customer_uuid;

    @CodeBuilderFilterDef(name = "status", type = "string", condition = "equals")
    public String status;


    @CodeBuilderFilterDef(name = "likestatus", type = "string", condition = "like")
    public String likestatus;

    @CodeBuilderFilterDef(name = "numberof", type = "int", condition = "lt")
    public int numberof;

    @CodeBuilderFilterDef(name = "weight", type = "big_decimal", condition = "gte")
    public BigDecimal weight;

}

