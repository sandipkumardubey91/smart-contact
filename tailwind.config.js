/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ["./src/main/resources/**/*.{html,js}"],
  safelist: ["sm:pl-64", "md:dark:bg-gray-700"],
  theme: {
    extend: {
      keyframes: {
        fadeDown: {
          "0%": { opacity: "0", transform: "translateY(-20px)" },
          "100%": { opacity: "1", transform: "translateY(0)" },
        },
        fadeIn: {
          "0%": { opacity: "0.05" },
          "100%": { opacity: "1" },
        },
        fadeTheme: {
          "0%": { opacity: "0.5" },
          "100%": { opacity: "1" },
        },
      },
      animation: {
        fadeDown: "fadeDown 0.6s ease-out",
        fadeIn: "fadeIn 0.8s ease-out forwards",
        fadeTheme: "fadeTheme 0.8s ease-in-out",
      },
    },
  },
  plugins: [],
  darkMode: "class",
};
