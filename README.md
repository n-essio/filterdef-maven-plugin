# Filterdef Maven plugin

It's a maven plugin to generate quickly java classes for our rest   controllers using with Quarkus and Hibernate Panache Project, with @Filters on Entity classes annotated. Normally we use this paradigm to developing quarkus rest app:
- entities with some hibernate filters:
```
@Entity
@Table(name = "customers")

@FilterDef(name = "obj.code", parameters = @ParamDef(name = "code", type = "string"))
@Filter(name = "obj.code", condition = "code = :code")

@FilterDef(name = "like.name", parameters = @ParamDef(name = "name", type = "string"))
@Filter(name = "like.name", condition = "lower(name) LIKE :name")

@FilterDef(name = "obj.active", parameters = @ParamDef(name = "active", type = "boolean"))
@Filter(name = "obj.active", condition = "active = :active")

public class Customer extends PanacheEntityBase {

    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(name = "uuid", unique = true)
    @Id
    public String uuid;
    public String code;
    public String name;
    public boolean active;
    public String ldap_group;
    public String mail;
}
```
- we will generating one rest controller for each entity, as: 

```
@Path(CUSTOMERS_PATH)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Singleton
public class CustomerServiceRs extends RsRepositoryServiceV3<Customer, String> {


    public CustomerServiceRs() {
        super(Customer.class);
    }

    @Override
    protected String getDefaultOrderBy() {
        return "name asc";
    }

    @Override
    public PanacheQuery<Customer> getSearch(String orderBy) throws Exception {
        PanacheQuery<Customer> search;
        Sort sort = sort(orderBy);

        if (sort != null) {
            search = Customer.find("select a from Customer a", sort);
        } else {
            search = Customer.find("select a from Customer a");
        }
        if (nn("obj.code")) {
            search
                    .filter("obj.code", Parameters.with("code", get("obj.code")));
        }
        if (nn("like.name")) {
            search
                    .filter("like.name", Parameters.with("name", likeParamToLowerCase("like.name")));
        }
        search.filter("obj.active", Parameters.with("active", true));
        return search;
    }

}
```

And the customer api, will be querable using:
```
https://prj.n-ess.it/api/v1/customers?obj.code=xxxx
```
The boring process is:
- the writing of hibernate filters
- the writing of search conditions using query parameters.
With our annotation set, we will generate at request using maven goal!

## How to install
Add to the pom.xml file, inside  ```<dependencies/>```:
```
 <dependency>
    <groupId>it.ness.codebuilder</groupId>
    <artifactId>filterdef-maven-plugin</artifactId>
    <version>1.0</version>
 </dependency>
```

Inside ```<build><plugins/>``` block, add this:

```
<plugin>
    <groupId>it.ness.codebuilder</groupId>
    <artifactId>filterdef-maven-plugin</artifactId>
    <version>1.0</version>
    <configuration>
        <removeAnnotations>false</removeAnnotations>
        <outputDirectory>src/main/java</outputDirectory>
    </configuration>
    <executions>
        <execution>
            <id>generate-sources</id>
            <phase>generate-sources</phase>
            <goals>
                <goal>generate-sources</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```
Configuration options:
- removeAnnotations (default-value="true")
  if you want rewrite your code (in src/main/java) you should use "removeAnnotations=false" - to review the original annotations
- outputDirectory  (default-value="target/generated-sources/codebuilder"):
  this is the place where the Model and Service classes will be generated.

## Java Model Annotations

Inside the Entity class you can use 3 annotations:

- CodeBuilderBooleanFilterDef
- CodeBuilderDefaultOrderBy
- CodeBuilderLikeStringFilterDef
- CodeBuilderListFilterDef
- CodeBuilderLocalDateTimeFilterDef
- CodeBuilderLog
- CodeBuilderLogicalDelete
- CodeBuilderNotNullStringFilterDef
- CodeBuilderNullBooleanFilterDef
- CodeBuilderNullStringFilterDef
- CodeBuilderOption
- CodeBuilderRsPath
- CodeBuilderStringFilterDef

**Description**

- CodeBuilderDefaultOrderBy.orderBy will be the default "order by" condition inside the rest class, for all queries.
  ```
    @CodeBuilderDefaultOrderBy(orderBy = "name asc")
  ```
  will generate in the Rest Service class:

  ```
    @Override
    protected String getDefaultOrderBy() {
      return "name asc";
    }
  ```

- CodeBuilderRsPath.path will be the path inside rest class.
  ```
    @CodeBuilderRsPath(path = "BLANK_DELIVERY_OPERATIONS_PATH")
  ```
  will generate in the Rest Service class:
  ```
    @Path(BLANK_DELIVERY_OPERATIONS_PATH)
  ```
- CodeBuilderFilterDef: name , type, condition
  - name is the name of filter
  - types can be: string or LocalDateTime or LocalDate or boolean or int or big_decimal
  - condition can be equals or like or not or lt, lte, gt, gte
  - options can be EXECUTE_ALWAYS or WITHOUT_PARAMETERS
  
Some examples:
 
- **empty values inside annotation:** 
  ```
    @CodeBuilderFilterDef()
	public String simplename;
  ```
will generate in the Entity class:
  ```
    @FilterDef(name = "obj.simplename", parameters = @ParamDef(name = "simplename", type = "string"))
    @Filter(name = "obj.simplename", condition = "simplename = :simplename")
  ```
will generate in the Rest Service class:
  ```
    if (nn("obj.simplename")) {
			search.filter("obj.simplename", Parameters.with("simplename", get("obj.simplename")));
	}
  ``` 
- **with all values inside annotation:**

 ```
	@CodeBuilderFilterDef(name = "status", type = "string", condition = "equals")
	public String status;
  ```
will generate in the Entity class:
  ```
    @FilterDef(name = "obj.status", parameters = @ParamDef(name = "status", type = "string"))
    @Filter(name = "obj.status", condition = "status = :status")
  ```
will generate in the Rest Service class:
  ```
    if (nn("obj.status")) {
			search.filter("obj.status", Parameters.with("status", get("obj.status")));
	}
  ``` 
- **with LocalDateTime type:**

 ```
    @CodeBuilderFilterDef(type = "LocalDateTime")
    public LocalDateTime simpledatetime;
  ```
will generate in the Entity class:
  ```
    @FilterDef(name = "from.simpledatetime", parameters = @ParamDef(name = "simpledatetime", type = "LocalDateTime"))
    @Filter(name = "from.simpledatetime", condition = "simpledatetime >= :simpledatetime")
    @FilterDef(name = "to.simpledatetime", parameters = @ParamDef(name = "simpledatetime", type = "LocalDateTime"))
    @Filter(name = "to.simpledatetime", condition = "simpledatetime <= :simpledatetime")
  ```
will generate in the Rest Service class:
  ```
    if (nn("from.simpledatetime")) {
			LocalDateTime date = LocalDateTime.parse(get("from.simpledatetime"));
			search.filter("from.simpledatetime", Parameters.with("simpledatetime", date));
    }
    if (nn("to.simpledatetime")) {
        LocalDateTime date = LocalDateTime.parse(get("to.simpledatetime"));
        search.filter("to.simpledatetime", Parameters.with("simpledatetime", date));
    }
  ```
- **with LocalDate type:**

 ```
	@CodeBuilderFilterDef(type = "LocalDate")
	public LocalDate simpledate;
  ```
will generate in the Entity class:
  ```
    @FilterDef(name = "from.simpledate", parameters = @ParamDef(name = "simpledate", type = "LocalDate"))
    @Filter(name = "from.simpledate", condition = "simpledate >= :simpledate")
    @FilterDef(name = "to.simpledate", parameters = @ParamDef(name = "simpledate", type = "LocalDate"))
    @Filter(name = "to.simpledate", condition = "simpledate <= :simpledate")
  ```
will generate in the Rest Service class:
  ```
    if (nn("from.simpledate")) {
			LocalDate date = LocalDate.parse(get("from.simpledate"));
			search.filter("from.simpledate", Parameters.with("simpledate", date));
    }
    if (nn("to.simpledate")) {
        LocalDate date = LocalDate.parse(get("to.simpledate"));
        search.filter("to.simpledate", Parameters.with("simpledate", date));
    }
  ```
- **condition without parameter:**
 ```     
	@CodeBuilderFilterDef(name = "customer_uuid", type = "string", condition = "not", options = {
			CodeBuilderOption.WITHOUT_PARAMETERS})
	public String customer_uuid;
  ```
will generate in the Entity class:
  ```
    @FilterDef(name = "not.parent_message_uuid")
    @Filter(name = "not.parent_message_uuid", condition = "parent_message_uuid  IS NULL")
  ```
will generate in the Rest Service class:
    ```	
	if (nn("not.customer_uuid")) {
			search.filter("not.customer_uuid");
	}
    ```
- **execute always:**
```
    @CodeBuilderFilterDef(options = {CodeBuilderOption.EXECUTE_ALWAYS})
	public boolean active;
  ```
will generate in the Entity class:
  ```
    @FilterDef(name = "obj.active", parameters = @ParamDef(name = "active", type = "boolean"))
    @Filter(name = "obj.active", condition = "active = :active")
  ```
will generate in the Rest Service class:
```
    search.filter("obj.active", Parameters.with("active", true));
 ```
- **int type with condition (value possibles: lt, lte, gt, gte):**
```
    @CodeBuilderFilterDef(name = "numberof", type = "int", condition = "lt")
    public int numberof;
  ```
will generate in the Entity class:
  ```
    @FilterDef(name = "obj.numberof", parameters = @ParamDef(name = "numberof", type = "int"))
    @Filter(name = "obj.numberof", condition = "numberof < :numberof")
  ```
will generate in the Rest Service class:
```
    if (nn("obj.numberof")) {
        Integer numberof = _integer("obj.numberof");
        search.filter("obj.numberof", Parameters.with("numberof", numberof));
    }
 ```
- **big_decimal type with condition (value possibles: lt, lte, gt, gte):**
```
    @CodeBuilderFilterDef(name = "weight", type = "big_decimal", condition = "gte")
    public BigDecimal weight;
  ```
will generate in the Entity class:
  ```
    @FilterDef(name = "obj.weight", parameters = @ParamDef(name = "weight", type = "big_decimal"))
    @Filter(name = "obj.weight", condition = "weight >= :weight")
  ```
will generate in the Rest Service class:
```
    if (nn("obj.weight")) {
        BigDecimal numberof = new BigDecimal(get("obj.weight"));
        search.filter("obj.weight", Parameters.with("weight", numberof));
    }
 ```

## Missing features

- list (panache dont supports filter setParameterList("states",List) )
  https://stackoverflow.com/questions/31597727/hibernate-filter-collection-of-enums
 ```
    @FilterDef(name = "byMultipleStates", defaultCondition = "status in (:states)", parameters = @ParamDef(name = "states", type = "string"))
    @Filter(name = "byMultipleStates", condition = "status in (:states)")
```



## Improuvements or TODO!

- [x] we need to check if model class contains already @Filter definition, before duplicate. (DONE)
- [x] we need to check if serviceRs exist and in this case, we should only replace the method getSearch(String orderBy)
- [x] we need to add more types (Integer or BigDecimal or List or Enumerated) with casting from String
- [ ] if the @CodeBuilderFilterDef is used on the class level, we should generate all possibile search using all fields
- [ ] if the entity don't contains CodeBuilderRsPath, we can try to generate in the rs a path in "standard way": /api/v1/{entity name in lowercase and plurals}  - ie: User => /api/v1/users
