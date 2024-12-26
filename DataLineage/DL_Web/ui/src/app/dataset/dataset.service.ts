/**
 *
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */


import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {IDataLineage, IPersistedDatasetDescriptor} from "../../generated-ts/lineage-model";
import {PromiseCache} from "../commons/promise-cache";

@Injectable()
export class DatasetService {
    private datasetPromiseCache = new PromiseCache<IPersistedDatasetDescriptor>()
    private overviewPromiseCache = new PromiseCache<IDataLineage>()

    constructor(private http: HttpClient) {
    }

    getDatasetDescriptor(id: string): Promise<IPersistedDatasetDescriptor> {
        return this.datasetPromiseCache.getOrCreate(id, () =>
            this.http.get<IPersistedDatasetDescriptor>(`rest/dataset/${id}/descriptor`).toPromise())
    }

    getLineageOverview(datasetId: string): Promise<IDataLineage> {
        const expandCacheForDatasetsRelatedTo = (lineagePromise: Promise<IDataLineage>) => (lineage: IDataLineage) => {
            lineage.datasets.forEach(ds => this.overviewPromiseCache.put(ds.id, lineagePromise))
            return lineage
        }

        const fetchAndExpandCache = () => {
            let lineagePromise = this.http.get<IDataLineage>(`rest/dataset/${datasetId}/lineage/overview`).toPromise()
            return lineagePromise.then(expandCacheForDatasetsRelatedTo(lineagePromise))
        }

        return this.overviewPromiseCache.getOrCreate(datasetId, fetchAndExpandCache)
    }
}