/*
 * Copyright 2017 EMBL - European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ebi.eva.t2d.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.eva.commons.models.data.Variant;
import uk.ac.ebi.eva.t2d.entity.DatasetMetadata;
import uk.ac.ebi.eva.t2d.entity.DatasetVersionMetadata;
import uk.ac.ebi.eva.t2d.entity.Phenotype;
import uk.ac.ebi.eva.t2d.entity.Property;
import uk.ac.ebi.eva.t2d.entity.Sample;
import uk.ac.ebi.eva.t2d.entity.SampleProperty;
import uk.ac.ebi.eva.t2d.entity.SamplesDatasetMetadata;
import uk.ac.ebi.eva.t2d.entity.VariantInfo;
import uk.ac.ebi.eva.t2d.model.T2DTableStructure;
import uk.ac.ebi.eva.t2d.model.T2dAnnotation;
import uk.ac.ebi.eva.t2d.repository.CommonSampleRepository;
import uk.ac.ebi.eva.t2d.repository.DatasetMetadataRepository;
import uk.ac.ebi.eva.t2d.repository.DatasetVersionMetadataRepository;
import uk.ac.ebi.eva.t2d.repository.PhenotypeRepository;
import uk.ac.ebi.eva.t2d.repository.PropertyRepository;
import uk.ac.ebi.eva.t2d.repository.PropertyToDatasetAndPhenotypeRepository;
import uk.ac.ebi.eva.t2d.repository.PropertyToDatasetRepository;
import uk.ac.ebi.eva.t2d.repository.SamplePropertyRepository;
import uk.ac.ebi.eva.t2d.repository.SamplePropertyToDatasetRepository;
import uk.ac.ebi.eva.t2d.repository.SamplesDatasetMetadataRepository;
import uk.ac.ebi.eva.t2d.repository.VariantInfoRepository;
import uk.ac.ebi.eva.t2d.utils.VariantUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

import static uk.ac.ebi.eva.pipeline.Application.T2D_PROFILE;
import static uk.ac.ebi.eva.t2d.configuration.T2dDataSourceConfiguration.T2D_PERSISTENCE_UNIT;
import static uk.ac.ebi.eva.t2d.configuration.T2dDataSourceConfiguration.T2D_TRANSACTION_MANAGER;
import static uk.ac.ebi.eva.t2d.utils.SqlUtils.sqlCreateTable;
import static uk.ac.ebi.eva.t2d.utils.SqlUtils.sqlInsert;

@Service
@Profile(T2D_PROFILE)
public class T2dJpaService implements T2dService {

    private static final Logger logger = LoggerFactory.getLogger(T2dJpaService.class);

    @PersistenceContext(unitName = T2D_PERSISTENCE_UNIT)
    private EntityManager entityManager;

    private final DatasetMetadataRepository datasetMetadataRepository;

    private final DatasetVersionMetadataRepository datasetVersionMetadataRepository;

    private final PropertyRepository propertyRepository;

    private final PropertyToDatasetRepository propertyToDatasetRepository;

    private final PhenotypeRepository phenotypeRepository;

    private final PropertyToDatasetAndPhenotypeRepository propertyToDatasetAndPhenotypeRepository;

    private final CommonSampleRepository commonSampleRepository;

    private final SamplesDatasetMetadataRepository samplesDatasetMetadataRepository;

    private final SamplePropertyRepository samplePropertyRepository;

    private final SamplePropertyToDatasetRepository samplePropertyToDatasetRepository;

    private final VariantInfoRepository variantInfoRepository;

    public T2dJpaService(DatasetMetadataRepository datasetMetadataRepository,
                         DatasetVersionMetadataRepository datasetVersionMetadataRepository,
                         PropertyRepository propertyRepository,
                         PropertyToDatasetRepository propertyToDatasetRepository,
                         PhenotypeRepository phenotypeRepository,
                         PropertyToDatasetAndPhenotypeRepository propertyToDatasetAndPhenotypeRepository,
                         CommonSampleRepository commonSampleRepository,
                         SamplesDatasetMetadataRepository samplesDatasetMetadataRepository,
                         SamplePropertyRepository samplePropertyRepository,
                         SamplePropertyToDatasetRepository samplePropertyToDatasetRepository,
                         VariantInfoRepository variantInfoRepository
    ) {
        this.datasetMetadataRepository = datasetMetadataRepository;
        this.datasetVersionMetadataRepository = datasetVersionMetadataRepository;
        this.propertyRepository = propertyRepository;
        this.propertyToDatasetRepository = propertyToDatasetRepository;
        this.phenotypeRepository = phenotypeRepository;
        this.propertyToDatasetAndPhenotypeRepository = propertyToDatasetAndPhenotypeRepository;
        this.commonSampleRepository = commonSampleRepository;
        this.samplesDatasetMetadataRepository = samplesDatasetMetadataRepository;
        this.samplePropertyRepository = samplePropertyRepository;
        this.samplePropertyToDatasetRepository = samplePropertyToDatasetRepository;
        this.variantInfoRepository = variantInfoRepository;
    }

    /**
     * TODO modify later to unify release of both metadata tables.
     *  @param datasetMetadata
     * @param metadata
     * @param datasetVersionMetadata
     */
    @Override
    @Modifying
    @Transactional(T2D_TRANSACTION_MANAGER)
    public void publishDataset(DatasetMetadata datasetMetadata, SamplesDatasetMetadata metadata, DatasetVersionMetadata datasetVersionMetadata) {
        datasetMetadataRepository.save(datasetMetadata);
        datasetVersionMetadataRepository.save(datasetVersionMetadata);
        samplesDatasetMetadataRepository.save(metadata);
    }

    @Override
    @Modifying
    @Transactional(T2D_TRANSACTION_MANAGER)
    public void createTable(T2DTableStructure tableStructure) {
        entityManager.createNativeQuery(sqlCreateTable(tableStructure)).executeUpdate();
    }

    @Override
    @Modifying
    @Transactional(T2D_TRANSACTION_MANAGER)
    public void insertSampleProperties(String datasetId, T2DTableStructure structure) {
        samplePropertyRepository.insertIfNotExists(SampleProperty.generate(structure));
        samplePropertyToDatasetRepository.save(datasetId, structure);
    }

    @Override
    @Modifying
    @Transactional(T2D_TRANSACTION_MANAGER)
    public void insertSampleProperties(String datasetId, T2DTableStructure structure, Phenotype phenotype) {
        propertyRepository.insertIfNotExists(Property.generate(structure));
        if (phenotype == null) {
            propertyToDatasetRepository.save(datasetId, structure);
        } else {
            phenotypeRepository.insertIfNotExists(phenotype);
            propertyToDatasetAndPhenotypeRepository.save(datasetId, structure, phenotype);
        }
    }

    @Override
    @Modifying
    @Transactional(T2D_TRANSACTION_MANAGER)
    public void insertData(T2DTableStructure tableStructure, List<? extends List<String>> data) {
        String query = sqlInsert(tableStructure, data);
        try {
            entityManager.createNativeQuery(query).executeUpdate();
        } catch (Exception ex) {
            logger.error("Original values: " + data);
            logger.error("SQL query: " + query);
            throw ex;
        }
    }

    @Override
    public boolean exists(Variant variant) {
        return variantInfoRepository.exists(VariantUtils.getVariantId(variant));
    }

    @Override
    @Modifying
    @Transactional(T2D_TRANSACTION_MANAGER)
    public void saveAnnotations(List<? extends T2dAnnotation> annotations) {
        variantInfoRepository.save((Iterable<VariantInfo>) annotations.stream().map(VariantInfo::new)::iterator);
    }

    @Override
    @Modifying
    @Transactional(T2D_TRANSACTION_MANAGER)
    public void insertSample(T2DTableStructure tableStructure, List<? extends List<String>> items) {
        List<String> fields = new ArrayList<>(tableStructure.getOrderedFieldIdSet());
        int idColumn = fields.indexOf("ID");
        Iterable<Sample> sampleIterable = items.stream().map(item -> new Sample(item.get(idColumn)))::iterator;
        commonSampleRepository.save(sampleIterable);
        insertData(tableStructure, items);
    }

}
