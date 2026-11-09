import { defineConfig, type Plugin } from 'vite'
import react from '@vitejs/plugin-react'
import os from 'os'

function networkAddressesPlugin(): Plugin {
  return {
    name: 'network-addresses',
    config(_, { command }) {
      if (command !== 'serve') return;

      const interfaces = os.networkInterfaces();
      const addresses: string[] = [];

      for (const iface of Object.values(interfaces)) {
        for (const entry of iface ?? []) {
          if (entry.family === 'IPv4' && !entry.internal) {
            addresses.push(entry.address);
          }
        }
      }

      return {
        define: {
          'import.meta.env.VITE_NETWORK_ADDRESSES': JSON.stringify(
            JSON.stringify(addresses)
          ),
        },
      };
    },
  };
}

export default defineConfig({
  plugins: [react(), networkAddressesPlugin()],
  server: {
    host: true,
  },
})