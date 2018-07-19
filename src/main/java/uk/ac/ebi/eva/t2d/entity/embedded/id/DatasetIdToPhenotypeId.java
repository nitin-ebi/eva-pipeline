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
package uk.ac.ebi.eva.t2d.entity.embedded.id;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * Composed ID DATASET / PHENOTYPE
 */
@Embeddable
public class DatasetIdToPhenotypeId implements Serializable {

    @Column(name = "ID")
    private String datasetId;

    @Column(name = "PH")
    private String phenotypeId;

    DatasetIdToPhenotypeId() {
    }

    public String getDatasetId() {
        return datasetId;
    }

    public String getPhenotypeId() {
        return phenotypeId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DatasetIdToPhenotypeId that = (DatasetIdToPhenotypeId) o;

        if (!getDatasetId().equals(that.getDatasetId())) return false;
        return getPhenotypeId().equals(that.getPhenotypeId());
    }

    @Override
    public int hashCode() {
        int result = getDatasetId().hashCode();
        result = 31 * result + getPhenotypeId().hashCode();
        return result;
    }
}
