(ns astro.routes.services.habits)

(def habits (atom {"run" {:name "run" :activities []}}))

(defn all-habits
  [context args value]
  (vals @habits))

(defn get-habit
  [context {:keys [name]} value]
  (first (filter #(= name (:name %)) @habits)))

(defn add-habit
  [context {:keys [name]} value]
  (let [new-habit {:name name :activities []}]
    (swap! habits assoc name new-habit)
    new-habit))

(defn add-activity
  [context {:keys [name date] :as payload} value]
  (swap! habits update-in [name :activities] conj date))
