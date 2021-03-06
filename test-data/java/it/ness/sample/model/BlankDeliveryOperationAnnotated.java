package it.ness.sample.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import it.coopservice.wastetracing.model.enums.BlankDeliveryOperationStatus;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

@Entity
@Table(name = "blank_delivery_operations")
@CodeBuilderDefaultOrderBy(orderBy = "uuid")
@CodeBuilderRsPath(path = "BLANK_DELIVERY_OPERATIONS_PATH")
@FilterDef(name = "obj.blankdeliveryschedule_uuid", parameters = @ParamDef(name = "blankdeliveryschedule_uuid", type = "string"))
@Filter(name = "obj.blankdeliveryschedule_uuid", condition = "blankdeliveryschedule_uuid = :blankdeliveryschedule_uuid")
@FilterDef(name = "from.operation_date", parameters = @ParamDef(name = "operation_date", type = "LocalDateTime"))
@Filter(name = "from.operation_date", condition = "operation_date >= :operation_date")
@FilterDef(name = "to.operation_date", parameters = @ParamDef(name = "operation_date", type = "LocalDateTime"))
@Filter(name = "to.operation_date", condition = "operation_date <= :operation_date")
public class BlankDeliveryOperationAnnotated extends PanacheEntityBase {

    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(name = "uuid", unique = true)
    @Id
    public String uuid;
    @CodeBuilderFilterDef(options = {CodeBuilderOption.EXECUTE_ALWAYS})
    public boolean active;
    public String company_uuid;
    @CodeBuilderFilterDef(options = {CodeBuilderOption.WITHOUT_PARAMETERS})
    public String customer_uuid;

    @CodeBuilderFilterDef()
    public String blankdeliveryschedule_uuid;
    public String costcenter_uuid;
    @CodeBuilderFilterDef(name ="site", type="string", condition="equals")
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

