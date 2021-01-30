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
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

@Entity
@Table(name = "all_options")
@CodeBuilderDefaultOrderBy(orderBy = "uuid asc")
@CodeBuilderRsPath(path = "ALL_OPTIONS")
@FilterDef(name = "from.simpledate", parameters = @ParamDef(name = "simpledate", type = "LocalDate"))
@Filter(name = "from.simpledate", condition = "simpledate >= :simpledate")
@FilterDef(name = "to.simpledate", parameters = @ParamDef(name = "simpledate", type = "LocalDate"))
@Filter(name = "to.simpledate", condition = "simpledate <= :simpledate")
@FilterDef(name = "not.customer_uuid")
@Filter(name = "not.customer_uuid", condition = "customer_uuid IS NULL")
@FilterDef(name = "like.likestatus", parameters = @ParamDef(name = "likestatus", type = "string"))
@Filter(name = "like.likestatus", condition = "lower(likestatus) LIKE :likestatus")
@FilterDef(name = "obj.active", parameters = @ParamDef(name = "active", type = "string"))
@Filter(name = "obj.active", condition = "active = :active")
@FilterDef(name = "obj.weight", parameters = @ParamDef(name = "weight", type = "big_decimal"))
@Filter(name = "obj.weight", condition = "weight >= :weight")
@FilterDef(name = "obj.simplename", parameters = @ParamDef(name = "simplename", type = "string"))
@Filter(name = "obj.simplename", condition = "simplename = :simplename")
@FilterDef(name = "from.simpledatetime", parameters = @ParamDef(name = "simpledatetime", type = "LocalDateTime"))
@Filter(name = "from.simpledatetime", condition = "simpledatetime >= :simpledatetime")
@FilterDef(name = "to.simpledatetime", parameters = @ParamDef(name = "simpledatetime", type = "LocalDateTime"))
@Filter(name = "to.simpledatetime", condition = "simpledatetime <= :simpledatetime")
@FilterDef(name = "obj.status", parameters = @ParamDef(name = "status", type = "string"))
@Filter(name = "obj.status", condition = "status = :status")
@FilterDef(name = "obj.numberof", parameters = @ParamDef(name = "numberof", type = "int"))
@Filter(name = "obj.numberof", condition = "numberof < :numberof")
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

	@CodeBuilderFilterDef(name = "customer_uuid", type = "string", condition = "not", options = {
			CodeBuilderOption.WITHOUT_PARAMETERS})
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
