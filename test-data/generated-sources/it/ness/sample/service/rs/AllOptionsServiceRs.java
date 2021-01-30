package it.ness.sample.service.rs;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;
import it.ness.api.service.RsRepositoryServiceV3;
import it.ness.sample.model.AllOptions;

import javax.ejb.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import static it.ness.sample.management.AppConstants.ALL_OPTIONS;
import java.time.LocalDateTime;
import java.time.LocalDate;

@Path(ALL_OPTIONS)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Singleton
public class AllOptionsServiceRs extends RsRepositoryServiceV3<AllOptions, String> {

	public AllOptionsServiceRs() {
		super(AllOptions.class);
	}

	@Override
	protected String getDefaultOrderBy() {
		return "uuid asc";
	}

	@Override
	public PanacheQuery<AllOptions> getSearch(String orderBy) throws Exception {
		PanacheQuery<AllOptions> search;
		Sort sort = sort(orderBy);
		if (sort != null) {
			search = AllOptions.find(null, sort);
		} else {
			search = AllOptions.find(null);
		}
		if (nn("from.simpledate")) {
			LocalDate date = LocalDate.parse(get("from.simpledate"));
			search.filter("from.simpledate", Parameters.with("simpledate", date));
		}
		if (nn("to.simpledate")) {
			LocalDate date = LocalDate.parse(get("to.simpledate"));
			search.filter("to.simpledate", Parameters.with("simpledate", date));
		}
		if (nn("not.customer_uuid")) {
			search.filter("not.customer_uuid");
		}
		if (nn("like.likestatus")) {
			search.filter("like.likestatus", Parameters.with("likestatus", likeParamToLowerCase("like.likestatus")));
		}
		search.filter("obj.active", Parameters.with("active", get("obj.active")));
		if (nn("obj.weight")) {
			BigDecimal numberof = new BigDecimal(get("obj.weight"));
			search.filter("obj.weight", Parameters.with("weight", numberof));
		}
		if (nn("obj.simplename")) {
			search.filter("obj.simplename", Parameters.with("simplename", get("obj.simplename")));
		}
		if (nn("from.simpledatetime")) {
			LocalDateTime date = LocalDateTime.parse(get("from.simpledatetime"));
			search.filter("from.simpledatetime", Parameters.with("simpledatetime", date));
		}
		if (nn("to.simpledatetime")) {
			LocalDateTime date = LocalDateTime.parse(get("to.simpledatetime"));
			search.filter("to.simpledatetime", Parameters.with("simpledatetime", date));
		}
		if (nn("obj.status")) {
			search.filter("obj.status", Parameters.with("status", get("obj.status")));
		}
		if (nn("obj.numberof")) {
			Integer numberof = _integer("obj.numberof");
			search.filter("obj.numberof", Parameters.with("numberof", numberof));
		}
		return search;
	}
}