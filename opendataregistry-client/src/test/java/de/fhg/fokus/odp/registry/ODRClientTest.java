/**
 * Copyright (c) 2012, 2013 Fraunhofer Institute FOKUS
 *
 * This file is part of Open Data Platform.
 *
 * Open Data Platform is free software: you can redistribute it and/or modify it under the terms of
 * the GNU Affero General Public License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 * 
 * Open Data Plaform is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with Open Data
 * Platform. If not, see <http://www.gnu.org/licenses/agpl-3.0>.
 */

package de.fhg.fokus.odp.registry;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fhg.fokus.odp.registry.ckan.ODRClientImpl;
import de.fhg.fokus.odp.registry.ckan.impl.DatasetImpl;
import de.fhg.fokus.odp.registry.ckan.impl.LicenceImpl;
import de.fhg.fokus.odp.registry.ckan.impl.MetadataImpl;
import de.fhg.fokus.odp.registry.ckan.impl.UserImpl;
import de.fhg.fokus.odp.registry.ckan.json.LicenceBean;
import de.fhg.fokus.odp.registry.common.TestBase;
import de.fhg.fokus.odp.registry.model.Application;
import de.fhg.fokus.odp.registry.model.Category;
import de.fhg.fokus.odp.registry.model.Contact;
import de.fhg.fokus.odp.registry.model.Dataset;
import de.fhg.fokus.odp.registry.model.Document;
import de.fhg.fokus.odp.registry.model.GeoGranularityEnumType;
import de.fhg.fokus.odp.registry.model.Licence;
import de.fhg.fokus.odp.registry.model.Metadata;
import de.fhg.fokus.odp.registry.model.MetadataEnumType;
import de.fhg.fokus.odp.registry.model.RoleEnumType;
import de.fhg.fokus.odp.registry.model.SectorEnumType;
import de.fhg.fokus.odp.registry.model.SpatialEnumType;
import de.fhg.fokus.odp.registry.model.Tag;
import de.fhg.fokus.odp.registry.model.TemporalGranularityEnumType;
import de.fhg.fokus.odp.registry.model.User;
import de.fhg.fokus.odp.registry.model.exception.OpenDataRegistryException;
import de.fhg.fokus.odp.registry.model.exception.UnknownRoleException;
import de.fhg.fokus.odp.registry.queries.Query;
import de.fhg.fokus.odp.registry.queries.QueryModeEnumType;
import de.fhg.fokus.odp.registry.queries.QueryResult;

public class ODRClientTest extends TestBase
{
  private final Logger log = LoggerFactory.getLogger(getClass());

  @Before
  public void beforeTest() throws Exception
  {
    setupConfigurationAndHttpServer();
  }

  @After
  public void tearDownHttpServer() throws Exception
  {
    stopHttpServer();
  }
  
  /*
   * Most important tests: listLicences(), getMetadata(), rateMetadata(), queryMetadata(),
   * listCategories(), userShow()
   */

  // ODR: listLicenses() - CKAN: license_list
  @Test
  public void listLicences()
  {
    List<Licence> licences = odrClient.listLicenses();

    Assertions.assertThat(licences).isNotNull();
    Assertions.assertThat(licences.size()).isEqualTo(37);

    Licence licence = licences.get(0);

    // name matches field "id" in json response
    Assertions.assertThat(licence.getName()).isEqualTo("dl-de-by-1.0");
    Assertions.assertThat(licence.getTitle()).isEqualTo("Datenlizenz Deutschland Namensnennung");
    Assertions.assertThat(licence.getUrl()).isEqualTo("https://www.govdata.de/dl-de/by-1-0");
    Assertions.assertThat(licence.getOther()).isNull();
    Assertions.assertThat(licence.isDomainContent()).isTrue();
    Assertions.assertThat(licence.isDomainData()).isTrue();
    Assertions.assertThat(licence.isDomainSoftware()).isFalse();
    Assertions.assertThat(licence.isOkdCompliant()).isTrue();
    Assertions.assertThat(licence.isOpen()).isTrue();
    Assertions.assertThat(licence.isOsiCompliant()).isFalse();
  }
  
  // ODR: getMetadata() - CKAN: package_show
  @Test
  public void getMetadata() throws OpenDataRegistryException
  {
    Metadata metadata = odrClient.getMetadata(null, "13dfb16a-c4f1-36b4-eda2-01ff5b1b294f");

    Assertions.assertThat(metadata).isNotNull();

    // Tests if subset of response data is correct
    Assertions.assertThat(metadata.getAuthor()).isEqualTo("Landesamt f\u00fcr Geologie und Bergbau");

    // Number of "groups"
    Assertions.assertThat(metadata.getCategories().size()).isEqualTo(2);
    // 3 contacts (author, publisher and maintainer)
    Assertions.assertThat(metadata.getContacts().size()).isEqualTo(3);
    Assertions.assertThat(metadata.getImages().size()).isEqualTo(0);
    Assertions.assertThat(metadata.getResources().size()).isEqualTo(3);
    // No subgroups in response
    Assertions.assertThat(metadata.getSubCategories().size()).isEqualTo(0);
    Assertions.assertThat(metadata.getTags().size()).isEqualTo(21);

    // Rating count from dataset_response.json "ratings_count"
    Assertions.assertThat(metadata.getRatingCount()).isEqualTo(0);

    Assertions.assertThat(metadata.isOpen()).isFalse();

    // Dataset has no related applications
    Assertions.assertThat(((Dataset) metadata).getRelatedApplications().size()).isEqualTo(0);
  }

  // ODR: loadRating() - CKAN: dataset
  @Test
  public void rateMetadata() throws OpenDataRegistryException
  {
    String name = "bremen";
    String metadataName = "13dfb16a-c4f1-36b4-eda2-01ff5b1b294f";
    User user = odrClient.findUser(name);

    Metadata metadata = odrClient.getMetadata(user, metadataName);
    odrClient.loadRating(metadata);

    Assertions.assertThat(user).isNotNull();
    Assertions.assertThat(((UserImpl) user).getApikey()).isNotNull();
    // Metadata has not been rated yet
    Assertions.assertThat(metadata.getRatingCount()).isEqualTo(0);
    Assertions.assertThat(metadata.getAverageRating()).isEqualTo(0.0);
  }

  // ODR: queryMetadata() - CKAN: package_search
  @Test
  public void queryMetadata()
  {
    Query query = new Query();
    QueryResult<Metadata> result = odrClient.queryMetadata(query);
    List<Metadata> metadata = result.getResult();

    Assertions.assertThat(result).isNotNull();
    Assertions.assertThat(metadata).isNotNull();
    // Query has no facets
    Assertions.assertThat(result.getFacets().size()).isEqualTo(0);
    Assertions.assertThat(metadata.size()).isEqualTo(10);
  }

  // ODR: listCategories() - CKAN: group_list
  @Test
  public void listCategories()
  {
    List<Category> categories = odrClient.listCategories();

    Assertions.assertThat(categories).isNotNull();
    // 14 different groups
    Assertions.assertThat(categories.size()).isEqualTo(14);

    for (Category category : categories)
    {
      Assertions.assertThat(category.getType()).isEqualTo("group");
      Assertions.assertThat(category.getCount()).isGreaterThanOrEqualTo(0);
    }
  }

  // ODR: findUser() - CKAN: user_show
  @Test
  public void userShow()
  {
    User user = odrClient.findUser("bremen");

    Assertions.assertThat(user).isNotNull();
    Assertions.assertThat(user.getName()).isEqualTo("bremen");
  }

  @Test
  public void listTags()
  {
    List<Tag> tags = odrClient.listTags();

    Assertions.assertThat(tags).isNotNull();
  }

  @Test
  public void getTagCounts()
  {
    @SuppressWarnings("deprecation")
    List<Tag> tags = odrClient.getTagCounts();

    for (Tag tag : tags)
    {
      log.info("{} : {}", tag.getName(), tag.getCount());
    }

    Assertions.assertThat(tags).isNotNull();
  }

  @Test
  public void querySearchTerm()
  {
    Query query = new Query();
    query.setSearchterm("Ulm");
    QueryResult<Metadata> result = odrClient.queryMetadata(query);

    if (result.isSuccess())
    {
      List<Metadata> metadata = result.getResult();

      for (Metadata set : metadata)
      {
        log.info("metadata: {}", set.getTitle());
      }
    }
    else
    {
      log.info("no success: {}", result.getErrorMessage());
    }

    Assertions.assertThat(result.isSuccess()).isTrue();
  }

  @Test
  public void extendedQueryMetadata()
  {
    Query query = new Query(QueryModeEnumType.EXTENDED);
    // query.getTypes().add(MetadataEnumType.APPLICATION);
    // query.setSearchterm("Bodenrichtwerte");
    // query.getCategories().add("geo");
    // query.setIsOpen(true);
    // List<Category> categories = odr.listCategories();
    // for (Category category : categories) {
    // query.getCategories().add(category.getName());
    // }
    // query.getTypes().add(MetadataEnumType.APPLICATION);
    // query.getTypes().add(MetadataEnumType.DOCUMENT);
    // query.getTypes().add(MetadataEnumType.DATASET);
    query.setMax(90);
    // query.setOffset(75);
    // query.getFormats().add("ASCII");
    // query.getSortFields().add("dates desc");

    QueryResult<Metadata> result = odrClient.queryMetadata(query);
    List<Metadata> metadata = result.getResult();
    log.info("metadata count: {}", metadata.size());

    for (Metadata set : metadata)
    {
      log.info("metadata typ {}: {} (open: {})",
          set.getType().toField(), set.getTitle(), String.valueOf(set.isOpen()));

      for (Contact contact : set.getContacts())
      {
        try
        {
          log.info("{}: {}", contact.getRole().getDisplayName(), contact.getName());
        }
        catch (UnknownRoleException e)
        {
          log.error("UnknownRoleException", e.getMessage());
        }
      }
      // log.info("created: {}", set.getCreatedAsString("dd.MM.yyyy"));
      // log.info("published: {}", set.getPublishedAsString("dd.MM.yyyy"));
      // log.info("modified: {}", set.getModifiedAsString("dd.MM.yyyy"));
      // String averageRating = set.getExtra("rateSum");
      // log.info("average rating is {}", averageRating);
    }

    Assertions.assertThat(metadata).isNotNull();
  }

  @Test
  public void getTag()
  {
    Tag tag = odrClient.getTag("Ozon");

    Assertions.assertThat(tag).isNotNull();
  }

  @Test
  public void status()
  {
    String status = odrClient.status();

    log.info("status of open data registry is: {}", status);
  }

  @Test
  public void createMetadata()
  {
    MetadataImpl impl = (MetadataImpl) odrClient.createMetadata(MetadataEnumType.DATASET);
    impl.setTitle("Test Create Metadata V");

    impl.setCreated(new Date());
    impl.setGeoCoverage("sumpfgebiete");
    impl.setGeoGranularity(GeoGranularityEnumType.CITY.toField());
    impl.setModified(new Date());
    impl.setNotes("Simple Metadata for testing.");
    impl.setPublished(new Date());
    impl.setSector(SectorEnumType.OTHER);
    impl.setTemporalGranularity(TemporalGranularityEnumType.MONTH);
    impl.setTemporalGranularityFactor(3);
    impl.setUrl("http://www.fokus.fraunhofer.de/elan");

    try
    {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
      impl.setTemporalCoverageFrom(sdf.parse("2012-06-01 00:00:00"));
      impl.setTemporalCoverageTo(sdf.parse("2013-06-01 00:00:00"));
    }
    catch (ParseException e)
    {
      e.printStackTrace();
    }

    LicenceBean bean = new LicenceBean();
    bean.setTitle("Creative Commons Attribution Share-Alike");
    bean.setId("cc-by-sa");
    bean.setUrl("http://creativecommons.org/licenses/by-sa/3.0/de");
    impl.setLicence(new LicenceImpl(bean));

    Contact publisher = impl.newContact(RoleEnumType.PUBLISHER);
    publisher.setAddress("Kaiserin-Augusta-Allee 31, 10589 Berlin, Deutschland");
    publisher.setEmail("publisher@fokus.fraunhofer.de");
    publisher.setName("I'm the Publisher");

    impl.getSpatialData().setType(SpatialEnumType.POLYGON);
    impl.getSpatialData().addPolygon();
    impl.getSpatialData().addCoordinate(0, 10.0000, 8.0000);
    impl.getSpatialData().addCoordinate(0, 10.3000, 8.5000);
    impl.getSpatialData().addCoordinate(0, 10.0000, 8.5000);
    impl.getSpatialData().addCoordinate(0, 10.3000, 8.0000);

    impl.setExtra("extra1", "value for extra 1");
    impl.setExtra("extra2", "value for extra 2");

    impl.newTag("test-create-4");

    User user = odrClient.findUser("sim");

    try
    {
      odrClient.persistMetadata(user, impl);
    }
    catch (OpenDataRegistryException e)
    {
      e.printStackTrace();
    }
  }

  @Test
  public void createUser()
  {
    String name = "testnutzer13";
    User user = odrClient.findUser(name);

    if (user == null)
    {
      log.info("User {} not found, creating...", name);
      user = odrClient.createUser(name, name + "@ogdd.de", name);
      if (user != null)
      {
        log.info("... done");
      }
    }
    else
    {
      log.info("User {} already exists", name);
    }
  }

  @Test
  public void getApplication()
  {
    try
    {
      Metadata m = odrClient.getMetadata(null, "de-hh-wms-inspire-a1-6-flurstueck");

      log.debug("metadata {} is of type {}", m.getName(), m.getType().toField());

      for (Application app : ((DatasetImpl) m).getRelatedApplications())
      {
        for (Dataset data : app.getRelatedDatasets())
        {
          log.debug("related application -> {}", data.getTitle());
        }
      }
    }
    catch (OpenDataRegistryException e)
    {
      e.printStackTrace();
    }
  }

  @Test
  public void listRelationships()
  {
    ((ODRClientImpl) odrClient).listRelationships("de-hh-inspire-flurstueck", null);
  }

  @Test
  public void fetchAll()
  {
    Query query = new Query();
    query.setMax(50);
    long start = System.currentTimeMillis();
    QueryResult<Metadata> result = odrClient.queryMetadata(query);
    long count = result.getCount();

    if (result.isSuccess())
    {
      long pages = result.getCount() / 50;

      for (int i = 1; i < pages; i++)
      {
        query.setPageoffset(i);
        result = odrClient.queryMetadata(query);

        for (Metadata m : result.getResult())
        {
          if (m.getLicence().getName() == null)
          {
            log.info("Found Metadata with null license: {} - {}", m.getName(), m.getTitle());
          }
          else if (m.getLicence().getName().isEmpty())
          {
            log.info("Found Metadata with empty license id: {} - {}", m.getName(), m.getTitle());
          }
        }
      }
    }
    log.info("fetched {} metadata in {} ms", count, System.currentTimeMillis() - start);
    System.out.println("fetched " + count + " metadata in " + (System.currentTimeMillis() - start) + " ms");
  }

  @Test
  public void getLatestDatasets()
  {
    Query query = new Query();

    query.getSortFields().add("metadata_modified desc");
    query.setMax(5);

    QueryResult<Dataset> result = odrClient.queryDatasets(query);

    Assertions.assertThat(result).isNotNull();
    Assertions.assertThat(result.isSuccess()).isTrue();
  }

  @Test
  public void getLatestDocuments()
  {
    Query query = new Query();

    query.getSortFields().add("metadata_modified desc");
    query.setMax(5);

    QueryResult<Document> result = odrClient.queryDocuments(query);

    Assertions.assertThat(result).isNotNull();
    Assertions.assertThat(result.isSuccess()).isTrue();
  }

  @Test
  public void getLatestApplications()
  {
    Query query = new Query();

    query.getSortFields().add("metadata_modified desc");
    query.setMax(5);

    QueryResult<Application> result = odrClient.queryApplications(query);

    Assertions.assertThat(result).isNotNull();
    Assertions.assertThat(result.isSuccess()).isTrue();
  }

}
