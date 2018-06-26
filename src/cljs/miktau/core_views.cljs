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
  [:div {:draggable false
         :on-drop
         (fn [e]
           (.call (aget e "preventDefault") e)
           (.log js/console "drop")
           (refe/dispatch [:generic/dragging? false]))}
   [ui-log-views/main]
   [:div
    (let [meta-page @(refe/subscribe [:meta])]
      (cond
        (=  (:page meta-page) :init)
        [generic-views/initial]
        
        (:loading? meta-page)
        [generic-views/processing]
        (:dragging? meta-page)
        [generic-views/dropzone]

        (= (:page meta-page) :tos)
        [generic-views/tos]
        (= (:page meta-page) :about)
        [generic-views/about]

        (= (:page meta-page) :oss-components)
        [generic-views/oss-components]
        
        
        
        (= (:page meta-page) :cloud)
        [cloud-views/main]
        
        (= (:page meta-page) :nodes)
        [nodes-views/main]
        (= (:page meta-page) :edit-nodes)
        [edit-nodes-views/main]
        (= (:page meta-page) :dropzone)
        [generic-views/dropzone]
        
        :else
        [generic-views/initial]))]])


