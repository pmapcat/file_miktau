(ns miktau.nodes.views
  (:require [miktau.generic.views-utils :as views-utils]
            [re-frame.core :as refe]
            [miktau.tools :as utils]))

(defn for-every-and-last
  [data-set]
  (let [last-by-index (dec (count data-set))]
    (for [[v k] (map list (range) data-set)]
      [k {:point k
          :index v
          :last? (= v last-by-index)
          :first? (= v 0)}])))


(defn radio-button
  [text on-change selected?]
  [:label.pure-checkbox
   {:for "blab" :style {:position "relative"}}
   [:input
    {:id "blab"  :style {:width "25px" :height "25px" :cursor "pointer"} :checked selected? :type "checkbox" :on-change on-change}]
   [:span {:style {:padding-bottom "5px"}}
    text]])
(defn- sortable-header [order-by]
  [:span
   (:name order-by) [:br]
   [:span {:style {:font-size "0.6em"}}
    "[ "
    (for [[_ point] (:items order-by)]
      [:a.unstyled-link
       {:href "#" :key (:name point) :on-click #(refe/dispatch (:on-click point)) :class (if (:enabled? point) " green-disabled " " black-clickable ")} (:name point) " "]) "]"]])

(defn file-table-header [all-selected? order-by]
  [:tr
   ;; select all nodes button
   [:th.mik-flush-left
    [radio-button "" #(refe/dispatch [:nodes/select-all-nodes]) all-selected?]]
   [:th.mik-flush-left
    [sortable-header (:name order-by)]]
   [:th]
   [:th.mik-flush-right
    [sortable-header (:modified order-by)]]])

(defn tagging-in-a-single-node-item
  [tags]
  [:span
   (for [[tag meta-tag] (for-every-and-last tags)]
     ^{:key (:key-name tag)}
     [:span
      [:a.tag
       {:on-click #(refe/dispatch [:nodes/clicked-cloud-item (tag :key-name)])
        :class (cond
                 (:selected? tag) "selected"
                 (:can-select? tag) "can-select"
                 :else "disabled")
        :style {:pointer "cursor"}}
       (:name tag)] (if-not (:last? meta-tag) "»" "")])])

(defn single-node-item [node]
  [:tr
   {:key (str  (:id node))
    :data-fpath  (str (:file-path node) (:name node))
    :style {:font-size "0.8em" :border-bottom "solid 1px #e3e3e3" :cursor "pointer"}
    :class (if (:selected? node) " node-selected " "")
    }
   [:td
    [radio-button "" #(refe/dispatch [:nodes/select-node (str (node :file-path) (node :name))]) (:selected? node)]]
   [:td
    [:a.unstyled-link.black-clickable
     {:href "#" :style {:font-weight "300" :word-wrap "break-word"}}
     (:name node)]]
   [:td
    [tagging-in-a-single-node-item (:tags node)]
    (if (not  (empty? (:all-tags node)))
      [:a.unstyled-link.black-clickable
       {:href "#"
        :on-click #(refe/dispatch [:nodes/clicked-many-cloud-items (:all-tags node)])}
       [:span
        {:style {:padding "3px" :margin "3px" :font-size "0.8em"}}
        [views-utils/icon "arrow_forward"]]]
      [:span])]
   [:td.mik-flush-right
    [:a.unstyled-link.blue-clickable {:href "#"
                       :on-click
                       #(refe/dispatch  [:nodes/click-on-fast-access-item (node :modified)])
                       :style {:font-weight "300"}}
     (str
      (utils/pad (:year   (node :modified)) 4 "0") "."
      (utils/pad (:month  (node :modified)) 2 "0") "."
      (utils/pad (:day    (node :modified)) 2 "0"))]]])

(defn file-table []
  (let [node-items  @(refe/subscribe [:nodes/node-items])
        order-by    @(refe/subscribe [:nodes/order-by])]
    ;; (if selection-mode?
    ;;   [:button.pure-button
    ;;    {:on-click #(refe/dispatch [:nodes/edit-nodes])} "Edit tags"]
    ;;   [:span])
    
    [:table {:style {:width "100%"}}
     [:thead
      [file-table-header  (:all-selected? node-items) order-by]]
     [:tbody
      (for [node (:nodes node-items)]
        ^{:key (str (:id node))}
        [single-node-item node])]
     [:tfoot
      [:tr [:td] [:td] [:td]
       [:td
        (if (> (:omitted-nodes node-items) 0)
          [:div.mik-flush-right.gray
           "Truncated: "
           [:b  (:omitted-nodes node-items)]]
          [:div])]]]]))

(defn main
  []
  [:div.pure-g
   [:div.pure-u-1.padded-as-button
    [file-table]]])




