export type Enum = {
    name: string;
    description: string;
}

export type ApplicationError = {
    error: any | AgendeSaudeApplicationError;
}

export type AgendeSaudeApplicationError = {
    title: string;
    status: number;
    details: string;
    developerMessage: string;
    className: string;
    timestamp: Date;
}

export type AuthenticationRequest = {
    login: string;
    password: string;
}

export type AuthenticationResponse = {
    accessToken: string;
    refreshToken: string;
}

export type PasswordReset = {
    hash: string;
    email: string;
    newPassword: string;
}

export type Token = {
    token: string;
    hash: string;
    type: string;
}

export type Page<T> = {
    content: T[];
    totalElements: number;
    totalPages: number;
    size: number;
    number: number;
    numberOfElements: number;
    first: boolean;
    last: boolean;
    empty: boolean;
}

export type CepRequestResponse = {
    cep: string;
    state: string;
    city: string;
    neighborhood: string;
    street: string;
}