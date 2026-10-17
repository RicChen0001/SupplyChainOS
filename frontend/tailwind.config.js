/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{ts,tsx}'],
  theme: {
    extend: {
      colors: {
        navy: { DEFAULT: '#0F2A44', 50: '#EEF3F8', 100: '#D9E4EF', 600: '#1B3A5C', 700: '#0F2A44', 900: '#0A1E32' },
      },
    },
  },
  plugins: [],
};
