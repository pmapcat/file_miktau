(ns miktau.core
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame]
            [miktau.events]
            [miktau.subs]
            [miktau.effects]
            [miktau.views.core :as views-core]
            [miktau.config :as config]))
;; (enable-console-print!)

(def ^:dynamic *server-call-params*
  {:prefix "/api/"
   :default-method :post
   :thread {} ;; state that works on many levels
   :default-loading-handler
   (fn [] [views/loading-view  "Loading sucks big time"])
   :default-error-handler
   (fn [response] [views/shitty-response "Bologoe" response])})

(awesome-lib/make-route
 "/cloud/:id/:app-params"
 (fn [_ server-response]))

(awesome-lib/make-route
 "/nodes/:id/:selected-items"
 (fn [_ server-response]))

(awesome-lib/make-route
 "/fuck/you/in/your/bloody/ass/"
 (fn [_ server-response]))

(awesome-lib/make-route
 "/HATE/CODING"
 (fn [_ server-response]
   [views/some-view (:batshit-crazy-shit server-response)]))

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
