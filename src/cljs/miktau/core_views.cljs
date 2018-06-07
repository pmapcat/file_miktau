(ns miktau.core-views
  (:require [miktau.edit-nodes.views :as edit-nodes-views]
            [miktau.nodes.views :as nodes-views]
            [miktau.cloud.views :as cloud-views]
            [miktau.ui-log.views :as ui-log-views]
            [miktau.generic.views :as generic-views]
            
            [day8.re-frame.http-fx]
            
            
            [miktau.api-handler.events]
            
            [miktau.generic.effects]
            [miktau.generic.coeffects]
            [miktau.generic.subs]
            [miktau.generic.events]
            
            [miktau.cloud.events]
            [miktau.cloud.subs]

            [miktau.autocomplete.events]
            [miktau.autocomplete.subs]
            
            [miktau.breadcrumbs.events]
            [miktau.breadcrumbs.subs]
            
            [miktau.nodes.events]
            [miktau.nodes.subs]
            
            [miktau.edit-nodes.events]
            [miktau.edit-nodes.subs]

            [miktau.ui-log.events]
            [miktau.ui-log.subs] 
           
            
            [re-frame.core :as refe]))

(defn main
  []
  [:div
   [ui-log-views/main]
   [:div
    (let [meta-page @(refe/subscribe [:meta])]
      (if (:loading? meta-page)
        [generic-views/processing]
        (condp = (:page meta-page)
          :cloud
          [cloud-views/main]
          :nodes
          [nodes-views/main]
          :edit-nodes
          [edit-nodes-views/main]
          :dropzone
          [generic-views/dropzone]
          [generic-views/initial])))]])


