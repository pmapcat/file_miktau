(ns miktau.core
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            
            [miktau.generic.effects]
            [miktau.generic.subs]
            [miktau.generic.events]
            
            [miktau.cloud.events]
            [miktau.cloud.subs]
            [miktau.nodes.events]
            [miktau.nodes.subs]
            [miktau.edit-nodes.events]
            [miktau.edit-nodes.subs]
            
            
            [miktau.core-views :as miktau-core-views]
            [miktau.config :as config]))

;; (enable-console-print!)


(defn dev-setup []
  (when config/debug?
    (enable-console-print!)
    (println "dev mode")))

(defn mount-root []
  (re-frame/clear-subscription-cache!)
  (reagent/render [miktau-core-views/main]
                  (.getElementById js/document "container")))

(defn render []
  (re-frame/dispatch-sync [:cloud/init-page #{:bibliostore} {:year 2017 :month 2 :day 9}])

  (dev-setup)
  (mount-root))
