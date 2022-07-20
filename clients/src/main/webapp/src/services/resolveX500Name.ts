export function resolveX500Name(name: string) {
    return name.split(',')[0].split('=')[1];
}