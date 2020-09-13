(ns astro.events
  (:require
   [re-frame.core :as rf]
   [ajax.core :as ajax]
   [reitit.frontend.easy :as rfe]
   [reitit.frontend.controllers :as rfc]))

;;dispatchers

(rf/reg-event-db
 :common/navigate
 (fn [db [_ match]]
   (let [old-match (:common/route db)
         new-match (assoc match :controllers
                          (rfc/apply-controllers (:controllers old-match) match))]
     (assoc db :common/route new-match))))

(rf/reg-fx
 :common/navigate-fx!
 (fn [[k & [params query]]]
   (rfe/push-state k params query)))

(rf/reg-event-fx
 :common/navigate!
 (fn [_ [_ url-key params query]]
   {:common/navigate-fx! [url-key params query]}))

(rf/reg-event-db
 :set-docs
 (fn [db [_ docs]]
   (assoc db :docs docs)))

(rf/reg-event-db
 :common/set-error
 (fn [db [_ error]]
   (assoc db :common/error error)))

(rf/reg-event-fx
 :page/init-home
 (fn [_ _]
   {:dispatch [:fetch-docs]}))

;;subscriptions

(rf/reg-sub
 :common/route
 (fn [db _]
   (-> db :common/route)))

(rf/reg-sub
 :common/page-id
 :<- [:common/route]
 (fn [route _]
   (-> route :data :name)))

(rf/reg-sub
 :common/page
 :<- [:common/route]
 (fn [route _]
   (-> route :data :view)))

(rf/reg-sub
 :common/error
 (fn [db _]
   (:common/error db)))

;; docs
(rf/reg-sub
 :docs
 (fn [db _]
   (:docs db)))

(rf/reg-event-fx
 :fetch-docs
 (fn [_ _]
   {:http-xhrio {:method          :get
                 :uri             "/docs"
                 :response-format (ajax/raw-response-format)
                 :on-success      [:set-docs]}}))

;; form
(rf/reg-sub
 :form.name
 (fn [db]
   (get-in db [:form :name])))

(rf/reg-event-db
 :form.name.changed
 (fn [db [_ name]]
   (assoc-in db [:form :name] name)))


;; habits


(rf/reg-sub
 :habits
 (fn [db]
   (:habits db)))

(defn add-habit-mutation
  [habit-name]
  {:query     "mutation AddHabit($name: String!) {
                add_habit(name: $name) {
                  name
                  activities
                }
              }"
   :variables {:name habit-name}})

(defn add-activity-mutation
  [habit-name]
  {:query     "mutation AddActivity($name: String!, $date: String!) {
                 add_activity(name: $name, date: $date) {
                   name
                   activities
                 }
               }"
   :variables {:name habit-name :date (.toISOString (js/Date.))}})

(rf/reg-event-fx
 :habit.new
 (fn [{:keys [db]} [_ habit-name]]
   {:http-xhrio
    {:method          :post
     :uri             "/api/graphql"
     :params          (add-habit-mutation habit-name)
     :format          (ajax/json-request-format)
     :response-format (ajax/json-response-format {:keywords? true})}
    :db (update db :habits assoc habit-name [])}))

(rf/reg-event-fx
 :habit.activity.new
 (fn [{:keys [db]} [_ habit-name]]
   {:http-xhrio
    {:method          :post
     :uri             "/api/graphql"
     :params          (add-activity-mutation habit-name)
     :format          (ajax/json-request-format)
     :response-format (ajax/json-response-format {:keywords? true})}
    :db (update-in db [:habits habit-name] conj (js/Date.))}))

(defn format-server-response
  [habit-list]
  (reduce
   (fn
     [memo {:keys [name activities]}]
     (assoc memo name (map #(js/Date. %) activities)))
   {}
   habit-list))

(rf/reg-event-db
 :set-habits
 (fn [db [_ payload]]
   (assoc db :habits (format-server-response (get-in payload [:data :habits])))))

(rf/reg-event-fx
 :fetch-habits
 (fn [_ _]
   {:http-xhrio {:method          :post
                 :uri             "/api/graphql"
                 :params          {:query "{ habits { name activities } }"}
                 :format          (ajax/json-request-format)
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-success      [:set-habits]}}))

(rf/dispatch [:fetch-habits])