(ns miktau.core
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            [miktau.events]
            [miktau.subs]
            [miktau.effects]
            [miktau.views.core :as views-core]
            [miktau.config :as config]))
;; (enable-console-print!)


(defn dev-setup []
  (when config/debug?
    (enable-console-print!)
    (println "dev mode")))

(defn mount-root []
  (re-frame/clear-subscription-cache!)
  (reagent/render [views-core/main]
                  (.getElementById js/document "container")))

(defn render []
  (re-frame/dispatch-sync [:init])

  (dev-setup)
  (mount-root))
