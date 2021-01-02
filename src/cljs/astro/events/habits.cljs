(ns astro.events.habits
  (:require
   [re-frame.core :as rf]
   [ajax.core :as ajax]))

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
