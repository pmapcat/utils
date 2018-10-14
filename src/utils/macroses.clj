(ns utils.macroses)

(defmacro catcher
  "evaluates body, on error, creates an exception, that binds to a variable declared in
  [err-binding]. Example 
  (catcher
   [blabus]
   (println (.getMessage blabus))
   (/ 1 0))"
  [err-binding on-error & body]
  (let [err-var (gensym 'error)]
    (list
     'try
     (cons 'do body)
     (list 'catch 'Exception err-var
           (list 'let [(first err-binding) err-var] on-error)))))


