import { describe, expect, it } from 'vitest';
import { formatNumber } from '@/lib/format';

describe('formatNumber', () => {
  it('formats with thousands separators', () => { expect(formatNumber(1234567)).toBe('1,234,567'); });
  it('renders null as an em dash', () => { expect(formatNumber(null)).toBe('—'); });
});
