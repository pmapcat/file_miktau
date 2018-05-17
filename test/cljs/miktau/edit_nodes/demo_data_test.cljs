(ns miktau.edit-nodes.demo-data-test)

(def demo-db
  {:core-directory ":test:"
   :nodes-selected #{"*"}
   :nodes-temp-tags-to-delete #{}
   :nodes-temp-tags-to-add    ""

   :meta {:page :edit-nodes}
   
   :total-nodes 0
   
   :cloud-selected #{:blab}
   :cloud-can-select {:blip true :blop true}
   :calendar-selected {:year  2018  :day 23 :month 11}})

(def initial-db-after-load-from-server
  {:nodes-selected #{"*"},
   :cloud-can-select {:moscow_market true, :devops true, :personal true, :usecases true, :биржа true, :amazon true, :магазины true, :wiki true, :work true, :sforim true, :согласовать true, :natan true, :работа_сделана true, :bibliostore true, :translator true, :скачка_источников true, :everybook true, :UI true, :blog true, :zeldin true},

   :meta {:page :edit-nodes}
   
   :total-nodes 22
   
   :nodes-temp-tags-to-add "",
   :nodes-temp-tags-to-delete #{},
   :cloud-selected #{:blab},
   :calendar-selected   {:year 2018, :day 23, :month 11},
   :core-directory "empty"})
