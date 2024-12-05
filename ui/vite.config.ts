import {defineConfig} from 'vite'
import react from '@vitejs/plugin-react-swc'

// https://vite.dev/config/
export default defineConfig({
    plugins: [react()],
    server: {
        proxy: {
            "/admin": {
                target: "http://localhost:8085",
                changeOrigin: true,
                secure: false,
            },
            "/logout": {
                target: "http://localhost:8085",
                changeOrigin: true,
                secure: false,
            },
        }
    }
})
