/*
 *
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */


import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {IDataLineage} from "../../generated-ts/lineage-model";
import {PromiseCache} from "../commons/promise-cache";

@Injectable()
export class LineageService {
    private lineagePromiseCache = new PromiseCache<IDataLineage>()

    constructor(private httpClient: HttpClient) {
    }

    getLineage(dsId: string): Promise<IDataLineage> {
        return this.lineagePromiseCache.getOrCreate(dsId, () =>
            this.httpClient.get<IDataLineage>(`rest/dataset/${dsId}/lineage/partial`).toPromise())
    }
}