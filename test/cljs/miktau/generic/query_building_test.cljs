(ns miktau.generic.query-building-test
  (:require-macros [cljs.test :refer [deftest testing is]])
  (:require [miktau.generic.demo-data-test :as demo-data]
            [miktau.generic.query-building :as query-building]))

(deftest test-building-query-for-retrieval []
  (let [db demo-data/initial-db-after-load-from-server]
    (is (= (query-building/build-core-query-for-retrieval nil)
           {:modified {}, :sorted "", :tags []}))
    
    (is (=  (query-building/build-core-query-for-retrieval (assoc db :nodes-selected #{"/blab" "/blip" "blob"}))
            {:modified {:year 2018, :day 23, :month 11}
             :sorted   ""
             :tags ["blab"]}))
    (is (=  (query-building/build-core-query-for-retrieval (assoc db :cloud-selected #{:nos :gos :dos}))
            {:modified {:year 2018, :day 23, :month 11},
             :sorted "",
             :tags ["dos" "gos" "nos"]}))
    (is (=  (query-building/build-core-query-for-retrieval (assoc db :nodes-sorted "-name"))
            {:modified {:year 2018, :day 23, :month 11}
             :sorted   "-name"
             :tags ["blab"]}))))

(deftest test-build-bulk-operate-on-files []
  (let [db  demo-data/initial-db-after-load-from-server]
    (is (= (query-building/build-bulk-operate-on-files db :zerio nil) nil))
    (is (= (query-building/build-bulk-operate-on-files db :symlinks nil)
           {:url "/api/bulk-operate-on-files"
            :params {:action "symlinks"
                     :request  {:modified {:year 2018, :day 23, :month 11}, :sorted "", :file-paths [], :tags ["blab"]}}}))
    (is (= (query-building/build-bulk-operate-on-files db nil nil) nil))))

(deftest test-build-switch-projects []
  (let [db  demo-data/initial-db-after-load-from-server]
    (is (= (query-building/build-switch-projects (assoc  db :core-directory "/home/mik/zero") nil)
           {:url "/api/switch-projects"
            :params {:file-path "/home/mik/zero"}}))
    (is (= (query-building/build-switch-projects (assoc db :core-directory nil) nil)
           nil))
    (is (= (query-building/build-switch-projects (assoc db :core-directory :zanoza) nil)
           nil))))

(deftest test-build-check-is-live []
  (is (= (query-building/build-check-is-live)
         {:url  "/api"
          :params {}})))

(deftest test-build-get-app-data []
  (let [db  demo-data/initial-db-after-load-from-server]
    (is (= (query-building/build-get-app-data db)
           {:url "/api/get-app-data"
            :params
            {:modified {:year 2018, :day 23, :month 11},
             :sorted "",
             :tags ["blab"]}}))
    (is (= (query-building/build-get-app-data nil)
           {:url "/api/get-app-data", :params {:modified {}, :sorted "", :tags []}}))))

(deftest test-build-update-records []
  (let [db  (assoc demo-data/initial-db-after-load-from-server
                   :nodes-temp-tags-to-add "a b c"
                   :nodes-temp-tags-to-delete #{:ho :no :so})]
    (is (= (query-building/build-update-records nil nil) nil))
    (is (= (query-building/build-update-records db nil)
           {:url "/api/update-records"
            :params {:tags-to-add ["a" "b" "c"]
                     :tags-to-delete ["ho" "no" "so"]
                     :request  {:modified {:year 2018, :day 23, :month 11}, :sorted "", :file-paths [], :tags ["blab"]}}}))
    (is (= (query-building/build-update-records (assoc db :nodes-temp-tags-to-add nil :nodes-temp-tags-to-delete nil) nil)
           nil))
    (is (= (query-building/build-update-records  nil nil) nil))))


(deftest test-building-query-for-action []
  (let [db demo-data/initial-db-after-load-from-server]
    (is (= (query-building/build-core-query-for-action {:nodes-selected #{"a" "b"}} nil)
           {:modified {}, :sorted "", :file-paths ["a" "b"], :tags []}))
    (is (=  (query-building/build-core-query-for-action (assoc db :nodes-selected #{"*" "/blab" "/blip" "blob"}) nil)
            {:modified {:year 2018, :day 23, :month 11}
             :sorted   ""
             :file-paths []
             :tags ["blab"]}))
    (is (=  (query-building/build-core-query-for-action db nil)
            {:modified {:year 2018, :day 23, :month 11}, :sorted "", :file-paths [], :tags ["blab"]}))
    (is (=  (query-building/build-core-query-for-action (assoc db :nodes-selected #{}) nil) nil))
    (is (=  (query-building/build-core-query-for-action {:nodes-selected #{}} nil) nil))
    (is (=  (query-building/build-core-query-for-action (assoc db :nodes-selected #{"*" "/blab" "/blip" "blob"} :nodes-sorted "-name") nil)
            {:modified {:year 2018, :day 23, :month 11}
             :sorted   ""
             :file-paths []
             :tags ["blab"]}))
    (is (=  (query-building/build-core-query-for-action (assoc db :nodes-selected #{"/blab" "/blip" "blob"}) nil)
            {:modified {}
             :sorted   ""
             :file-paths ["/blab" "/blip" "blob"]
             :tags []}))
    (is (=  (query-building/build-core-query-for-action (assoc db :nodes-selected #{}) nil) nil))))

