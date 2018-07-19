/*
 * Copyright 2016-2017 EMBL - European Bioinformatics Institute
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
package uk.ac.ebi.eva.t2d.entity;

import uk.ac.ebi.eva.t2d.entity.embedded.id.PropertyIdDatasetIdPhenotypeId;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * JPA definition for the table with the relationship between Datasets and Phenotypes
 */
@Entity
@Table(name = "META_PROP_DATASET_PH")
public class PropertyToDatasetAndPhenotype {

    @EmbeddedId
    private PropertyIdDatasetIdPhenotypeId id;

    private static final String NULL = "NULL";

    @Column(name = "TRUST_COL")
    private String trustColumn;

    PropertyToDatasetAndPhenotype() {
    }

    public PropertyToDatasetAndPhenotype(String propertyId, String datasetId, String phenotypeId) {
        id = new PropertyIdDatasetIdPhenotypeId(propertyId, datasetId, phenotypeId);
        trustColumn = NULL;
    }

    public String getDatasetId() {
        return id.getDatasetId();
    }
}
