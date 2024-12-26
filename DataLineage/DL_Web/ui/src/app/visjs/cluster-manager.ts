/*
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */


import * as vis from "vis";
import * as _ from "lodash";
import {VisClusterNode, VisModel} from "./vis-model";

export type Clusterize<TVisNode, TVisEdge> = (nodes: TVisNode[], edges: TVisEdge[]) => VisClusterNode<TVisNode>[]

export class ClusterManager<TVisNode, TVisEdge> {

    private clusters: VisClusterNode<TVisNode>[]

    constructor(private graph: VisModel<TVisNode, TVisEdge>,
                private network: vis.Network,
                private clusterize: Clusterize<TVisNode, TVisEdge>) {
    }

    public rebuildClusters() {
        this.clusters = this.clusterize(
            this.graph.nodes.get(),
            this.graph.edges.get())
    }

    public refreshHighlightedClustersForNodes() {
        this.rebuildClusters()
        this.clusters.forEach(c => {
            if (this.network.isCluster(c.id))
            // we can't use updateClusteredNode() method as it emits "_dataChanged" event that makes the graph shaking
                (<any>this.network).clustering.body.nodes[c.id].setOptions(c)
        })
    }

    public collapseAllClusters() {
        this.clusters.forEach(c => this.collapseCluster(c))
    }

    public collapseClustersExceptForNode(nodeId: string) {
        this.clusters
            .filter(c => !_.some(c.nodes, {id: nodeId}))
            .forEach(c => this.collapseCluster(c))
    }

    private collapseCluster(cluster: VisClusterNode<TVisNode>) {
        this.network.cluster({
            clusterNodeProperties: cluster,
            joinCondition: nodeOps => _.some(cluster.nodes, {id: nodeOps.id})
        })
    }

    public expandClusterForNode(nodeId: string) {
        this.clusters
            .filter(c => _.some(c.nodes, {id: nodeId}))
            .forEach(c => this.expandCluster(c))
    }

    private expandCluster(cluster: VisClusterNode<TVisNode>) {
        try {
            this.network.openCluster(cluster.id)
        } catch (e) {
            // todo: fix clustering rather than swallowing this error
        }
    }
}
