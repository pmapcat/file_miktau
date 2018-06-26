(ns miktau.core
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame]

            [miktau.generic.subs]
            [miktau.file-api.menu :as electron-menu]
            
            [miktau.core-views :as miktau-core-views]
            [miktau.config :as config]))

;; (enable-console-print!)


(defn dev-setup []
  (when config/debug?
    (enable-console-print!)
    (println "dev mode")))

(defn mount-root []
  (re-frame/clear-subscription-cache!)
  (electron-menu/set-menu!)
  
  (.log js/console "BABABA")
  (reagent/render [miktau-core-views/main]
                  (.getElementById js/document "container")))
(defn render []
  ;; (:core-dir @(re-frame/subscribe [:generic/test-db]))

  ;; (re-frame/dispatch-sync [:i])
  ;; (re-frame/dispatch [:cloud/init-page-no-undo #{}])
  ;; (re-frame/dispatch-sync [:nodes/init-page #{}  #{:bibliostore}])
  ;; (re-frame/dispatch-sync [:nodes/init-page #{}  #{}])
  ;; (re-frame/dispatch-sync [:edit-nodes/init-page #{"*"}  #{:bibliostore}])
  ;; (re-frame/dispatch-sync [:ui-log/register-error "Error view sample"])  
  (dev-setup)
  (mount-root))
