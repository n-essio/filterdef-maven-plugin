package it.ness.sample.service.rs;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;
import it.ness.api.service.RsRepositoryServiceV3;
import it.ness.sample.model.BlankDeliveryOperationAnnotated;

import javax.ejb.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import static it.ness.sample.management.AppConstants.BLANK_DELIVERY_OPERATIONS_PATH;
import java.time.LocalDateTime;

@Path(BLANK_DELIVERY_OPERATIONS_PATH)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Singleton
public class BlankDeliveryOperationAnnotatedServiceRs
		extends
			RsRepositoryServiceV3<BlankDeliveryOperationAnnotated, String> {

	public BlankDeliveryOperationAnnotatedServiceRs() {
		super(BlankDeliveryOperationAnnotated.class);
	}

	@Override
	protected String getDefaultOrderBy() {
		return "uuid";
	}

	@Override
	public PanacheQuery<BlankDeliveryOperationAnnotated> getSearch(String orderBy) throws Exception {
		PanacheQuery<BlankDeliveryOperationAnnotated> search;
		Sort sort = sort(orderBy);
		if (sort != null) {
			search = BlankDeliveryOperationAnnotated.find(null, sort);
		} else {
			search = BlankDeliveryOperationAnnotated.find(null);
		}
		if (nn("obj.site")) {
			search.filter("obj.site", Parameters.with("site", get("obj.site")));
		}
		if (nn("obj.customer_uuid")) {
			search.filter("obj.customer_uuid");
		}
		if (nn("obj.blankdeliveryschedule_uuid")) {
			search.filter("obj.blankdeliveryschedule_uuid",
					Parameters.with("blankdeliveryschedule_uuid", get("obj.blankdeliveryschedule_uuid")));
		}
		search.filter("obj.active", Parameters.with("active", get("obj.active")));
		if (nn("from.operation_date")) {
			LocalDateTime date = LocalDateTime.parse(get("from.operation_date"));
			search.filter("from.operation_date", Parameters.with("operation_date", date));
		}
		if (nn("to.operation_date")) {
			LocalDateTime date = LocalDateTime.parse(get("to.operation_date"));
			search.filter("to.operation_date", Parameters.with("operation_date", date));
		}
		return search;
	}
}