import { useState } from "react";
import "./NetworkAddresses.css";

function buildUrls(): string[] {
    const port = window.location.port ? `:${window.location.port}` : "";
    const protocol = window.location.protocol;

    const urls: string[] = [`${protocol}//localhost${port}`];

    try {
        const raw = import.meta.env.VITE_NETWORK_ADDRESSES as string | undefined;
        if (raw) {
            const ips: string[] = JSON.parse(raw);
            ips.forEach((ip) => urls.push(`${protocol}//${ip}${port}`));
        }
    } catch {
        // ignore parse errors
    }

    return urls;
}

export function NetworkAddresses() {
    const [copied, setCopied] = useState<string | null>(null);

    // Only show in dev mode
    if (!import.meta.env.DEV) return null;

    const urls = buildUrls();

    const handleCopy = async (url: string) => {
        await navigator.clipboard.writeText(url);
        setCopied(url);
        setTimeout(() => setCopied(null), 2000);
    };

    return (
        <div className="network-addresses" role="region" aria-label="Server addresses">
            <span className="network-label">🌐 Share this address:</span>
            <div className="network-url-list">
                {urls.map((url) => (
                    <button
                        key={url}
                        className={`network-url-btn${copied === url ? " copied" : ""}`}
                        onClick={() => handleCopy(url)}
                        title="Click to copy"
                    >
                        <span className="network-url-text">{url}</span>
                        <span className="network-copy-icon">
                            {copied === url ? "✓" : "⎘"}
                        </span>
                    </button>
                ))}
            </div>
        </div>
    );
}
