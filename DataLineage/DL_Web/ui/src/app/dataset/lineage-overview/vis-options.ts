/*
 *
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */


import {Options} from "vis";

export const visOptions: Options = {

    autoResize: true,
    interaction: {
        hover: true,
        selectConnectedEdges: false,
        hoverConnectedEdges: false
    },
    layout: {
        hierarchical: {
            enabled: true,
            sortMethod: 'directed',
            direction: 'UD',
            parentCentralization: true,
            nodeSpacing: 200,
            levelSeparation: 200
        }
    },
    physics: {
        enabled: true,
        hierarchicalRepulsion: {
            nodeDistance: 270,
            springLength: 170,
            springConstant: 10,
            damping: 1
        }
    },

    edges: {
        color: {
            color: '#E0E0E0',
            hover: '#E0E0E0',
            highlight: 'E0E0E0'
        },
        shadow: false,
        width: 10,
        arrows: "to",
        font: {
            color: '#343434',
            background: '#ffffff',
            strokeWidth: 0
        }
    },
    nodes: {
        shape: 'icon',
        shadow: false,
        // margin: 10,
        labelHighlightBold: false,
        font: {
            color: '#343434',
            size: 20
        }
    }
}
