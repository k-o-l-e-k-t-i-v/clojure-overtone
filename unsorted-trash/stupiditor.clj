;; Version 1.0beta25
;; http://github.com/overtone/emacs-live
;; hello everyone)

(def b (control-bus))
(def b2 (control-bus))
(defsynth rt [rate 20]
  (out:kr b (impulse:kr rate))
  (mix))

(definst s [freq 30
            div  1
            bus  0
            fdec 0.1
            adec 0.2
            facc 300
            nfreq 200
            namp 0.1
            nadec 0.07]
  (let [dtun [1 1.02 1.07 2.03]
        time (pulse-divider:kr (in:kr bus) div)
        fenv (+ freq (* facc (decay time fdec)))
        src  (+ (sin-osc fenv) (* (* namp (decay time nadec)) (lf-noise0 (+ nfreq fenv))))
        amp  (decay time adec)
        sig  (* src amp)
        sig  (rhpf sig fenv 0.9)
        sig (free-verb sig 0.3 0.7 0.1)]
    (pan2 sig 0 1)))

(def t (rt))
(ctl t2 :rate 7)
(def c (mouse-x:kr 30 140))
(def k1 (s :bus b :div 12 :facc 100 :namp 0.00))
(def k2 (s :bus b :div 2 :facc -100 :fdec 0.03 :adec 0.06 :freq c))

(defsynth mouser [min 30 max 300]
  (out:kr b2 (mouse-x:kr min max)))

(kill rt)
(kill k2)
(ctl s :div 32)

()
