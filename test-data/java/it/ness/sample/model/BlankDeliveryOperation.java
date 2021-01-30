package it.ness.sample.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import it.coopservice.wastetracing.model.enums.BlankDeliveryOperationStatus;
import org.hibernate.annotations.GenericGenerator;
import it.ness.codebuilder.annotations.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "blank_delivery_operations")
@CodeBuilderDefaultOrderBy(orderBy = "uuid")
@CodeBuilderRsPath(path = "BLANK_DELIVERY_OPERATIONS_PATH")
public class BlankDeliveryOperation extends PanacheEntityBase {

    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(name = "uuid", unique = true)
    @Id
    public String uuid;
    @CodeBuilderFilterDef(options = {CodeBuilderOption.EXECUTE_ALWAYS})
    public boolean active;
    public String company_uuid;
    @CodeBuilderFilterDef(name = "customer_uuid", type = "string", condition = "not", options = {CodeBuilderOption.WITHOUT_PARAMETERS})
    public String customer_uuid;

    @CodeBuilderFilterDef()
    public String blankdeliveryschedule_uuid;
    public String costcenter_uuid;
    @CodeBuilderFilterDef(name = "site", type = "string", condition = "equals")
    public String site_uuid;

    @CodeBuilderFilterDef(type = "LocalDateTime")
    public LocalDateTime operation_date;
    public String hour_interval;
    public String notes;

    // IF SPOT
    public String requestor;
    public boolean spot;

    @OneToMany
    List<BlankDeliveryOperationData> blankDeliveryOperationDataList;

    // STATUS - CALCULATED USING OPERATIONS
    @Enumerated(EnumType.STRING)
    BlankDeliveryOperationStatus blankDeliveryOperationStatus;

}

