(ns miktau.api-handler.events-test
  (:require-macros [cljs.test :refer [deftest testing is]])
  (:require [miktau.api-handler.events :as events]
            [miktau.test-utils :refer [with-diff]]))

(deftest test-get-app-data []
  (with-diff (select-keys (:http-xhrio (events/get-app-data {} [nil :on-success "-name" #{"*"} #{:blab} {:year 2018, :day 23, :month 11}] {})) [:params :on-success :on-failure])
    {:params {:modified {:year 2018, :day 23, :month 11}, :sorted "-name", :file-paths [], :tags ["blab"] :page-size 10 :page 1}, :on-success [:api-handler/got-app-data :on-success], :on-failure [:error]}))

(deftest test-got-app-data []
  (with-diff (events/got-app-data {} [nil :on-success {:response true :error nil}])
    {:db nil, :fx-redirect [:on-success {:response true, :error nil}]})
  (with-diff (events/got-app-data {} [nil :on-success {:response true :error "No connection to inner self"}])
    {:db nil, :fx-redirect [:error "No connection to inner self"]}))


(deftest test-file-operation []
  (with-diff (events/file-operation {} [nil :on-success :rm-rf #{"*"} #{:blab} {:year 2018, :day 23, :month 11}])
    {:db nil, :fx-redirect [:error "Cannot build request on these params"]})
  (with-diff (select-keys (:http-xhrio (events/file-operation {} [nil :on-success :filebrowser #{"*"} #{:blab} {:year 2018, :day 23, :month 11}])) [:params :on-success :on-failure])
    {:params {:action "filebrowser", :request {:modified {:year 2018, :day 23, :month 11}, :sorted "", :file-paths [], :tags ["blab"]}}, :on-success [:api-handler/got-app-data :on-success], :on-failure [:error]}))

(deftest test-build-update-records []
  (with-diff (select-keys
              (:http-xhrio (events/build-update-records {} [nil :on-success "tag_a tag_b tag_c" #{:ha :ho} #{"*"} #{:blab} {:year 2018, :day 23, :month 11}]))
              [:params :on-success :on-failure])
    {:params {:tags-to-add ["tag_a" "tag_b" "tag_c"], :tags-to-delete ["ha" "ho"], :request {:modified {:year 2018, :day 23, :month 11}, :sorted "", :file-paths [], :tags ["blab"]}}, :on-success [:api-handler/got-app-data :on-success], :on-failure [:error]}))
