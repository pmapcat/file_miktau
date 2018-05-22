(ns miktau.core-views
  (:require [miktau.edit-nodes.views :as edit-nodes-views]
            [miktau.nodes.views :as nodes-views]
            [miktau.cloud.views :as cloud-views]
            [miktau.generic.views :as generic-views]

            [day8.re-frame.http-fx]
            
            [miktau.api-handler.events]
            
            [miktau.generic.effects]
            [miktau.generic.subs]
            [miktau.generic.events]
            
            [miktau.cloud.events]
            [miktau.cloud.subs]
            
            [miktau.nodes.events]
            [miktau.nodes.subs]
            
            [miktau.edit-nodes.events]
            [miktau.edit-nodes.subs]
            
            
            [re-frame.core :as refe]))

(defn main
  []
  [:div
   ;; [:a {:href "#" :on-click #(refe/dispatch [:cloud/init-page #{:bibliostore} {}]) } "cloud"]
   ;; [:a {:href "#" :on-click #(refe/dispatch [:nodes/init-page #{} #{} {}]) } "node items"]
   ;; [:a {:href "#" :on-click #(refe/dispatch [:edit-nodes/init-page #{"*"} #{:bibliostore} {}]) } "nodes edit"]
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
          [generic-views/choose-root])))]])


