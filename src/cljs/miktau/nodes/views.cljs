(ns miktau.nodes.views
  (:require [miktau.generic.views-utils :as views-utils]
            [re-frame.core :as refe]
            [miktau.utils :as utils]))

(defn radio-button
  [text on-change selected?]
  [:label.pure-checkbox
   {:for "blab" :style {:position "relative"}}
   [:input
    {:id "blab"  :style {:width "25px" :height "25px" :cursor "pointer"} :checked selected? :type "checkbox" :on-change on-change}]
   [:span {:style {:padding-bottom "5px"}}
    text]])

(defn file-table-header [selection-mode? all-selected?]
  [:div.pure-g {:style {:padding-bottom "1em"}}
   ;; select all nodes button
   [:div.pure-u-2-24
    (views-utils/position-absolute
     {:top "0px"}
     [radio-button "" #(refe/dispatch [:nodes/select-all-nodes]) all-selected?])]
   [:div.pure-u-16-24
    [:div
     "Name"
     [:span {:style {:font-size "0.6em"}}
      "["
      [:a.unstyled-link {:href "#" :key "order-a-z"  :on-click #(refe/dispatch [:nodes/sort "name"])} "a-z"]
      "·"
      [:a.unstyled-link {:href "#" :key "order-z-a" :on-click #(refe/dispatch [:nodes/sort "-name"])} " z-a"] "]"]]]
   [:div.pure-u-6-24.mik-flush-right
    [:div
     [:span {:style {:font-size "0.6em"}}
      "["
      [:a.unstyled-link {:href "#" :key "order-a-z" :on-click #(refe/dispatch [:nodes/sort "modified"])} "recent"]
      "·"
      [:a.unstyled-link {:href "#" :key "order-z-a" :on-click #(refe/dispatch [:nodes/sort "-modified"])} " older"] "]"]
     "Modified"]]])

(defn tagging-in-a-single-node-item
  [tags selection-mode?]
  [:div
   (for [tag tags]
     [:a.inline-tag
      {:key (:key-name tag)
       :on-click #(refe/dispatch [:nodes/clicked-cloud-item (tag :key-name)])
       :class
       (str
        (cond (:to-delete? tag) "crossed-out"
              (:to-add?    tag) "added-in"
              :else "")
        " "
        (cond
          (:selected? tag) "selected"
          (:can-select? tag) "can-select"
          :else "disabled"))
       :style {:pointer "cursor"}}  (:name tag) " "])])

(defn single-node-item [node selection-mode?]
  [:tr
   {:key (str  (:id node))
    :data-fpath  (str (:file-path node) (:name node))
    :style {:padding-bottom "10px"
            :padding-top "10px"
            :font-size "0.8em"
            :border-bottom "solid 1px #e3e3e3"
            :cursor "pointer"}}
   [:td
    [views-utils/position-absolute
     {:top ""}
     [radio-button "" #(refe/dispatch [:nodes/select-node (str (node :file-path) (node :name))]) (:selected? node)]]]
   [:td
    [:a.unstyled-link
     {:href "#" :style {:font-weight "300" :word-wrap "break-word"}}
     (:name node)]]
   [:td
    [tagging-in-a-single-node-item (:tags node) selection-mode?]
    (cond
      selection-mode?
      [:span]
      (not  (empty? (:all-tags node)))
      [:a.unstyled-link
       {:href "#"
        :on-click
        (if-not selection-mode?
          #(refe/dispatch [:nodes/clicked-many-cloud-items (:all-tags node)])
          identity)}
       [:span
        {:style {:padding "3px" :margin "3px" :font-size "0.8em"}}
        [views-utils/icon "arrow_forward"]]]
      :else
      [:span])]
   [:td
    [:a.unstyled-link {:href "#"
                       :on-click
                       (if-not selection-mode? #(refe/dispatch  [:nodes/click-on-fast-access-item (node :modified)]) identity)
                       :style {:font-weight "300"}}
     (str
      (utils/pad (:year   (node :modified)) 4 "0") "."
      (utils/pad (:month  (node :modified)) 2 "0") "."
      (utils/pad (:day    (node :modified)) 2 "0"))]]])

(defn file-table []
  (let [node-items  @(refe/subscribe [:nodes/node-items])
        selection-mode? @(refe/subscribe [:nodes/selection-mode?])]
    [:div.padded-as-button.background-1
     [file-table-header selection-mode? (:all-selected? node-items)]
     (if selection-mode?
       [:button.pure-button
        {:on-click #(refe/dispatch [:nodes/edit-nodes])} "Edit tags"]
       [:span])
     [:table
      [:tbody
       (for [node (:nodes node-items)]
         ^{:key (str (:id node))}
         [single-node-item node selection-mode?])]]
     (if (> (:omitted-nodes node-items) 0)
       [:div.mik-flush-right.gray
        "Truncated: "
        [:b  (:omitted-nodes node-items)]]
       [:div])]))

(defn main
  []
  [:div.background-2
   [:div.pure-g
    [:div.pure-u-1.padded-as-button
     [file-table]]]])




